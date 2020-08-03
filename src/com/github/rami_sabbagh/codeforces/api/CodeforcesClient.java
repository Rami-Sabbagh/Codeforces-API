package com.github.rami_sabbagh.codeforces.api;

import com.github.rami_sabbagh.codeforces.api.objects.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A Codeforces API client.
 */
public class CodeforcesClient {
    /**
     * The base URL for the Codeforces API.
     */
    public static final String baseURL = "https://codeforces.com/api/";
    /**
     * The Gson instance for deserializing the requests responses.
     */
    private static final Gson gson = new Gson();
    /**
     * The characters set used for generating a 6 random characters String for the authorization process.
     */
    private static final char[] randomCharset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    /**
     * The authorization apiKey, can be <i>null</i> for no authorization.
     */
    private final String apiKey;
    /**
     * The authorization apiSection, must be null when apiKey is, and vice versa.
     */
    private final String apiSecret;
    /**
     * The HttpClient for executing the API requests.
     */
    private final HttpClient httpClient;

    /**
     * Creates a new CodeforcesClient with default configuration.
     */
    private CodeforcesClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = null;
        this.apiSecret = null;
    }

    /**
     * Creates a new CodeforcesClient with custom configuration.
     *
     * @param httpClient The HttpClient for executing the API requests.
     * @param apiKey     The authorization apiKey, can be <i>null</i> for no authorization.
     * @param apiSecret  The authorization apiSection, must be null when apiKey is, and vice versa.
     */
    private CodeforcesClient(HttpClient httpClient, String apiKey, String apiSecret) {
        this.httpClient = httpClient;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    /**
     * URL encodes a string value using `UTF-8` encoding scheme.
     * Sourced from https://www.urlencoder.io/java/
     *
     * @param value The value to encode.
     * @return The value URL encoded.
     */
    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    /**
     * Calculates SHA-512 of a string.
     * Sourced from https://www.geeksforgeeks.org/sha-512-hash-in-java/.
     *
     * @param input The input string to hash.
     * @return The SHA-512 hash in HEX format.
     */
    private static String calculateSHA512(String input) {
        try {
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            StringBuilder hashText = new StringBuilder(no.toString(16));

            // Add preceding 0s to make it 32-bit
            while (hashText.length() < 32)
                hashText.insert(0, "0");

            // Return the hashText
            return hashText.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Returns a new CodeforcesClient with default settings.
     * <p>
     * Equivalent to newBuilder.build();
     * <p>
     * The default settings include: no authorization, no timeout and default proxy selector.
     *
     * @return a new CodeforcesClient.
     */
    public static CodeforcesClient newCodeforcesClient() {
        return new CodeforcesClient();
    }

    /**
     * Create a new CodeforcesClient builder.
     *
     * @return a CodeforcesClient.Builder.
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Generates a 6 characters string of random characters for usage in authorization.
     *
     * @return A 6 characters string of random characters.
     */
    private static String generateRandomString() {

        //Generate 6 random characters.
        char[] result = new char[6];
        for (int i = 0; i < 6; i++)
            result[i] = randomCharset[Math.abs(ThreadLocalRandom.current().nextInt() % randomCharset.length)];

        //Convert them into a string.
        return String.valueOf(result);
    }

    /**
     * Executes a Codeforces API HTTP request.
     *
     * @param methodName The name of the API method.
     * @param parameters The parameters of the API method.
     * @param <R>        The result type of the method.
     * @return The result of the method.
     * @throws IOException          When the HTTP API connection fails.
     * @throws InterruptedException When the thread is interrupted during the request.
     * @throws CFException          When the Codeforces API responses with a failure.
     */
    private <R> R request(String methodName, SortedMap<String, String> parameters, Class<R> type) throws IOException, InterruptedException, CFException {
        String endpoint = (apiKey == null) ? getEndpoint(methodName, parameters) : getAuthorizedEndpoint(methodName, parameters);
        URI requestURI = URI.create(baseURL + endpoint);

        HttpRequest request = HttpRequest.newBuilder(requestURI).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Type resultType = TypeToken.getParameterized(Result.class, type).getType();
        Result<R> result = gson.fromJson(response.body(), resultType);

        if (result.status == Result.Status.FAILED)
            throw new CFException(result.comment);

        return type.cast(result.result);
    }

    /**
     * Returns information about one or several users.
     *
     * @param handles Semicolon-separated list of handles. No more than 10000 handles is accepted.
     * @return A list of User objects for requested handles.
     * @throws InterruptedException When the thread is interrupted during the request.
     * @throws CFException          When the Codeforces API responses with a failure.
     * @throws IOException          When the HTTP API connection fails.
     */
    public User[] requestUsersInformation(String handles) throws InterruptedException, CFException, IOException {
        SortedMap<String, String> parameters = new TreeMap<>();
        parameters.put("handles", handles);
        return request("user.info", parameters, User[].class);
    }

    /**
     * Formats a map of url parameters into a string.
     *
     * @param parameters The parameters.
     * @return The parameters url encoded.
     */
    private String formatParameters(Map<String, String> parameters) {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, String> parameter : parameters.entrySet())
            builder.append(parameter.getKey()).append('=').append(encodeValue(parameter.getValue())).append('&');
        builder.setLength(builder.length() - 1); //Delete the last '&'.

        return builder.toString();
    }

    /**
     * Formats the normal endpoint (without authorization) for a Codeforces request.
     *
     * @param methodName The request's method name.
     * @param parameters The parameters of the request.
     * @return The endpoint with parameters encoded.
     */
    private String getEndpoint(String methodName, Map<String, String> parameters) {
        return methodName + '?' + formatParameters(parameters);
    }

    /**
     * Calculates and formats the authorized endpoint for a Codeforces request.
     * Note: <i>apiKey</i> and <i>time</i> parameters will be added.
     * References: https://codeforces.com/apiHelp
     *
     * @param methodName The request's method name.
     * @param parameters The parameters of the request.
     * @return The authorized endpoint with parameters encoded, valid for about 5 minutes.
     */
    private String getAuthorizedEndpoint(String methodName, SortedMap<String, String> parameters) {
        //The timestamp for the authorization.
        String time = String.valueOf(System.currentTimeMillis() / 1000);

        //Add the apiKey and time to the parameters.
        parameters.put("apiKey", apiKey);
        parameters.put("time", time);

        //Format the parameters into a string.
        String formattedParameters = formatParameters(parameters);

        //The random string for the apiSig.
        String rand = generateRandomString();

        //The base string for apiSig.
        String hashString = String.format("%s/%s?%s#%s", rand, methodName, formattedParameters, apiSecret);

        //The calculated apiSig.
        String apiSig = rand + calculateSHA512(hashString);

        //Format the authorized request endpoint.
        return String.format("%s?%s&apiSig=%s", methodName, formattedParameters, apiSig);
    }

    /**
     * A CodeforcesClient builder.
     */
    public static class Builder {
        private final HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
        private String apiKey;
        private String apiSecret;

        private Builder() {
        }

        /**
         * Disables the connect timeout duration for this client.
         *
         * @return This builder.
         */
        public Builder timeout() {
            httpClientBuilder.connectTimeout(null);
            return this;
        }

        /**
         * Sets the connect timeout duration for this client.
         *
         * @param timeout The duration to allow the underlying connection to be established.
         * @return This builder.
         */
        public Builder timeout(Duration timeout) {
            httpClientBuilder.connectTimeout(timeout);
            return this;
        }

        /**
         * Sets the connect timeout duration for this client.
         *
         * @param seconds The duration to allow the underlying connection to be established in seconds.
         * @return This builder.
         */
        public Builder timeout(long seconds) {
            httpClientBuilder.connectTimeout(Duration.ofSeconds(seconds));
            return this;
        }

        /**
         * Sets a ProxySelector.
         *
         * @param proxySelector The proxySelector.
         * @return This builder.
         */
        public Builder proxy(ProxySelector proxySelector) {
            httpClientBuilder.proxy(proxySelector);
            return this;
        }

        /**
         * Resets to the default proxy selector.
         *
         * @return This builder.
         */
        public Builder proxy() {
            httpClientBuilder.proxy(ProxySelector.getDefault());
            return this;
        }

        /**
         * Sets the requests authorization for this client.
         * All methods can be requested anonymously. This way only public data will be accessible via API.
         * To access data, private for some user (e.g. hacks during the contest),
         * The API key can be generated on https://codeforces.com/settings/api page.
         *
         * @param apiKey    The authorization apiKey, can be null for no authorization.
         * @param apiSecret The authorization apiSecret, must be null when apiKey is, and vice versa.
         * @return This builder.
         */
        public Builder authorization(String apiKey, String apiSecret) {
            if (apiKey == null && apiSecret == null) {
                this.apiKey = null;
                this.apiSecret = null;
            } else if (apiKey != null & apiSecret != null) {
                this.apiKey = apiKey;
                this.apiSecret = apiSecret;
            } else if (apiKey == null) {
                throw new IllegalArgumentException("apiKey is null, while apiSecret is not!");
            } else {
                throw new IllegalArgumentException("apiSecret is null, while apiKey is not!");
            }

            return this;
        }

        /**
         * Disables the requests authorization for this client.
         *
         * @return This builder.
         */
        public Builder authorization() {
            this.apiKey = null;
            this.apiSecret = null;
            return this;
        }

        /**
         * Returns a new CodeforcesClient built from the current state of the builder.
         *
         * @return a new CodeforcesClient.
         */
        public CodeforcesClient build() {
            return new CodeforcesClient(httpClientBuilder.build(), apiKey, apiSecret);
        }
    }

    /**
     * An API request result.
     *
     * @param <T> The type of the result object.
     */
    protected static class Result<T> {
        /**
         * The status of the request, either OK or FAILED.
         */
        Status status;

        /**
         * If the status is <i>FAILED</i> then comment contains the reason why the request failed.
         * If the status os <i>OK</i>, then there is no comment.
         */
        String comment;

        /**
         * The result of the request, null on when the status is <i>FAILED</i>.
         */
        T result;

        /**
         * The request's status enum.
         */
        protected enum Status {
            OK, FAILED
        }
    }
}
