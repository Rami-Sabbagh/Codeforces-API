package com.github.rami_sabbagh.codeforces.api;

import com.github.rami_sabbagh.codeforces.api.objects.*;
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
 * <p>
 * A CodeforcesClient can be used to send API requests and retrieve their responses.
 * There are 2 ways to create a Client, with by creating a one with default configuration using {@code .newCodeforcesClient()}
 * Or by using {@code .newBuilder()}, changing the configuration and {@code .build()}.
 * <p>
 * With Codeforces API you can get access to some of their data as objects.
 * Each request call is an HTTP request to their API and thus requires an internet connection.
 * <p>
 * The requests my fail to connections issues, or due to invalid arguments or such.
 * <p>
 * The connection issues would result in an IOException, while API failures would result in a CFException.
 * <p>
 * API may be requested at most 5 times in one second.
 * If you send more requests, the requests will throw a CFException with "Call limit exceeded" comment.
 *
 * <p><b>Simple Example</b>
 * <pre>{@code     CodeforcesClient client = CodeforcesClient.newCodeforcesClient()
 *    User[] user = client.requestUsersInformation("my_username");
 *    System.out.println(user.toStringPretty());}</pre>
 *
 * <p><b>Authorization Example</b>
 * <pre>{@code     Codeforces client = CodeforcesClient.newBuilder()
 *                                        .authorization("api_key","api_secret")
 *                                        .build();
 *    String[] friends = client.requestUserFriends("my_username");
 *    System.out.println("Friends:");
 *    for (String friend : friends)
 *        System.out.println("- " + friend);}</pre>
 *
 * @author Rami Sabbagh (@Rami-Sabbagh)
 * @version 1.0.0
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
     * The language code for Language-depended fields like names or descriptions.
     */
    private final String lang;
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
        this.lang = "en";
        this.apiKey = null;
        this.apiSecret = null;
    }

    /**
     * Creates a new CodeforcesClient with custom configuration.
     *
     * @param httpClient The HttpClient for executing the API requests.
     * @param lang       The language to use for Language-depended fields like names or descriptions.
     * @param apiKey     The authorization apiKey, can be <i>null</i> for no authorization.
     * @param apiSecret  The authorization apiSection, must be null when apiKey is, and vice versa.
     */
    private CodeforcesClient(HttpClient httpClient, String lang, String apiKey, String apiSecret) {
        this.httpClient = httpClient;
        this.lang = lang;
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
        //TODO: Rate-limit system.
        parameters.put("lang", lang);

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
     * Returns a list of comments to the specified blog entry.
     *
     * @param blogEntryId Id of the blog entry. It can be seen in blog entry URL. For example: /blog/entry/79
     * @return A list of Comment objects.
     * @throws InterruptedException When the thread is interrupted during the request.
     * @throws CFException          When the Codeforces API responses with a failure.
     * @throws IOException          When the HTTP API connection fails.
     */
    public Comment[] requestBlogEntryComments(int blogEntryId) throws InterruptedException, CFException, IOException {
        SortedMap<String, String> parameters = new TreeMap<>();
        parameters.put("blogEntryId", String.valueOf(blogEntryId));
        return request("blogEntry.comments", parameters, Comment[].class);
    }

    /**
     * Returns blog entry.
     *
     * @param blogEntryId Id of the blog entry. It can be seen in blog entry URL. For example: /blog/entry/79
     * @return A BlogEntry object in full version.
     * @throws InterruptedException When the thread is interrupted during the request.
     * @throws CFException          When the Codeforces API responses with a failure.
     * @throws IOException          When the HTTP API connection fails.
     */
    public BlogEntry requestBlogEntry(int blogEntryId) throws InterruptedException, CFException, IOException {
        SortedMap<String, String> parameters = new TreeMap<>();
        parameters.put("blogEntryId", String.valueOf(blogEntryId));
        return request("blogEntry.view", parameters, BlogEntry.class);
    }

    /**
     * Returns list of hacks in the specified contests.
     * Full information about hacks is available only after some time after the contest end.
     * During the contest user can see only own hacks.
     *
     * @param contestId Id of the contest. It is not the round number. It can be seen in contest URL. For example: /contest/566/status
     * @return A list of Hack objects.
     * @throws InterruptedException When the thread is interrupted during the request.
     * @throws CFException          When the Codeforces API responses with a failure.
     * @throws IOException          When the HTTP API connection fails.
     */
    public Hack[] requestContestHacks(int contestId) throws InterruptedException, CFException, IOException {
        SortedMap<String, String> parameters = new TreeMap<>();
        parameters.put("contestId", String.valueOf(contestId));
        return request("contest.hacks", parameters, Hack[].class);
    }

    /**
     * Returns information about all available contests.
     *
     * @param gym If <i>true</i> then gym contests are returned. Otherwise, regular contests are returned.
     * @return A list of Contest objects. If this method is called not anonymously, then all available contests for a calling user will be returned too, including mashups and private gyms.
     * @throws InterruptedException When the thread is interrupted during the request.
     * @throws CFException          When the Codeforces API responses with a failure.
     * @throws IOException          When the HTTP API connection fails.
     */
    public Contest[] requestContestsList(Boolean gym) throws InterruptedException, CFException, IOException {
        SortedMap<String, String> parameters = new TreeMap<>();
        if (gym != null) parameters.put("gym", String.valueOf(gym));
        return request("contest.list", parameters, Contest[].class);
    }

    /**
     * Returns rating changes after the contest.
     *
     * @param contestId Id of the contest. It is not the round number. It can be seen in contest URL. For example: /contest/566/status
     * @return A list of RatingChange objects.
     * @throws InterruptedException When the thread is interrupted during the request.
     * @throws CFException          When the Codeforces API responses with a failure.
     * @throws IOException          When the HTTP API connection fails.
     */
    public RatingChange[] requestContestRatingChanges(int contestId) throws InterruptedException, CFException, IOException {
        SortedMap<String, String> parameters = new TreeMap<>();
        parameters.put("contestId", String.valueOf(contestId));
        return request("contest.ratingChanges", parameters, RatingChange[].class);
    }

    /**
     * Returns the description of the contest and the requested part of the standings.
     *
     * @param contestId      Id of the contest. It is not the round number. It can be seen in contest URL. For example: /contest/566/status
     * @param from           (optional) (can be null) 1-based index of the standings row to start the ranklist.
     * @param count          (optional) (can be null) Number of standing rows to return.
     * @param handles        (optional) (can be null) Semicolon-separated list of handles. No more than 10000 handles is accepted.
     * @param room           (optional) (can be null) If specified, than only participants from this room will be shown in the result. If not — all the participants will be shown.
     * @param showUnofficial (optional) (can be null) If <i>true</i> than all participants (virtual, out of competition) are shown. Otherwise, only official contestants are shown.
     * @return The contest standings.
     * @throws InterruptedException When the thread is interrupted during the request.
     * @throws CFException          When the Codeforces API responses with a failure.
     * @throws IOException          When the HTTP API connection fails.
     */
    public ContestStandings requestContestStandings(int contestId, Integer from, Integer count, String handles, Integer room, Boolean showUnofficial) throws InterruptedException, CFException, IOException {
        SortedMap<String, String> parameters = new TreeMap<>();
        parameters.put("contestId", String.valueOf(contestId));
        if (from != null) parameters.put("from", String.valueOf(from));
        if (count != null) parameters.put("count", String.valueOf(count));
        if (handles != null) parameters.put("handles", handles);
        if (room != null) parameters.put("room", String.valueOf(room));
        if (showUnofficial != null) parameters.put("showUnofficial", String.valueOf(showUnofficial));
        return request("contest.standings", parameters, ContestStandings.class);
    }

    /**
     * Returns submissions for specified contest. Optionally can return submissions of specified user.
     *
     * @param contestId Id of the contest. It is not the round number. It can be seen in contest URL. For example: /contest/566/status
     * @param handle    (optional) (can be null) Codeforces user handle.
     * @param from      (optional) (can be null) 1-based index of the first submission to return.
     * @param count     (optional) (can be null) Number of returned submissions.
     * @return A list of Submission objects, sorted in decreasing order of submission id.
     * @throws InterruptedException When the thread is interrupted during the request.
     * @throws CFException          When the Codeforces API responses with a failure.
     * @throws IOException          When the HTTP API connection fails.
     */
    public Submission[] requestContestStatus(int contestId, String handle, Integer from, Integer count) throws InterruptedException, CFException, IOException {
        SortedMap<String, String> parameters = new TreeMap<>();
        parameters.put("contestId", String.valueOf(contestId));
        if (handle != null) parameters.put("handle", handle);
        if (from != null) parameters.put("from", String.valueOf(from));
        if (count != null) parameters.put("count", String.valueOf(count));
        return request("contest.status", parameters, Submission[].class);
    }

    /**
     * Returns all problems from problemset. Problems can be filtered by tags.
     *
     * @param tags           (optional) (can be null) Semicolon-separated list of tags.
     * @param problemsetName (optional) (can be null) Custom problemset's short name, like 'acmsguru'
     * @return The ProblemSet.
     * @throws InterruptedException When the thread is interrupted during the request.
     * @throws CFException          When the Codeforces API responses with a failure.
     * @throws IOException          When the HTTP API connection fails.
     */
    public ProblemSet requestProblemSet(String tags, String problemsetName) throws InterruptedException, CFException, IOException {
        SortedMap<String, String> parameters = new TreeMap<>();
        if (tags != null) parameters.put("tags", tags);
        if (problemsetName != null) parameters.put("problemsetName", problemsetName);
        return request("problemset.problems", parameters, ProblemSet.class);
    }

    /**
     * Returns recent submissions.
     *
     * @param count          Number of submissions to return. Can be up to 1000.
     * @param problemsetName (optional) (can be null) Custom problemset's short name, like 'acmsguru'
     * @return A list of Submission objects, sorted in decreasing order of submission id.
     * @throws InterruptedException When the thread is interrupted during the request.
     * @throws CFException          When the Codeforces API responses with a failure.
     * @throws IOException          When the HTTP API connection fails.
     */
    public Submission[] requestProblemSetRecentStatus(int count, String problemsetName) throws InterruptedException, CFException, IOException {
        SortedMap<String, String> parameters = new TreeMap<>();
        parameters.put("count", String.valueOf(count));
        if (problemsetName != null) parameters.put("problemsetName", problemsetName);
        return request("problemset.recentStatus", parameters, Submission[].class);
    }

    /**
     * Returns recent actions.
     *
     * @param maxCount Number of recent actions to return. Can be up to 100.
     * @return A list of RecentAction objects.
     * @throws InterruptedException When the thread is interrupted during the request.
     * @throws CFException          When the Codeforces API responses with a failure.
     * @throws IOException          When the HTTP API connection fails.
     */
    public RecentAction[] requestRecentActions(int maxCount) throws InterruptedException, CFException, IOException {
        SortedMap<String, String> parameters = new TreeMap<>();
        parameters.put("maxCount", String.valueOf(maxCount));
        return request("recentActions", parameters, RecentAction[].class);
    }

    /**
     * Returns a list of all user's blog entries.
     *
     * @param handle Codeforces user handle.
     * @return A list of BlogEntry objects in short form.
     * @throws InterruptedException When the thread is interrupted during the request.
     * @throws CFException          When the Codeforces API responses with a failure.
     * @throws IOException          When the HTTP API connection fails.
     */
    public BlogEntry[] requestUserBlogEntries(String handle) throws InterruptedException, CFException, IOException {
        if (handle == null) throw new NullPointerException("handle is null!");
        SortedMap<String, String> parameters = new TreeMap<>();
        parameters.put("handle", handle);
        return request("user.blogEntries", parameters, BlogEntry[].class);
    }

    /**
     * Returns authorized user's friends. Using this method requires authorization.
     *
     * @param onlyOnline If <i>true</i> only online friends are returned. Otherwise, all friends are returned.
     * @return Returns a list of strings — users' handles.
     * @throws InterruptedException When the thread is interrupted during the request.
     * @throws CFException          When the Codeforces API responses with a failure.
     * @throws IOException          When the HTTP API connection fails.
     */
    public String[] requestUserFriends(Boolean onlyOnline) throws InterruptedException, CFException, IOException {
        SortedMap<String, String> parameters = new TreeMap<>();
        if (onlyOnline != null) parameters.put("onlyOnline", String.valueOf(onlyOnline));
        return request("user.friends", parameters, String[].class);
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
        if (handles == null) throw new NullPointerException("handles is null!");
        SortedMap<String, String> parameters = new TreeMap<>();
        parameters.put("handles", handles);
        return request("user.info", parameters, User[].class);
    }

    /**
     * Returns the list users who have participated in at least one rated contest.
     *
     * @param activeOnly If true then only users, who participated in rated contest during the last month are returned. Otherwise, all users with at least one rated contest are returned.
     * @return A list of User objects, sorted in decreasing order of rating.
     * @throws InterruptedException When the thread is interrupted during the request.
     * @throws CFException          When the Codeforces API responses with a failure.
     * @throws IOException          When the HTTP API connection fails.
     */
    public User[] requestRatedUsersList(Boolean activeOnly) throws InterruptedException, CFException, IOException {
        SortedMap<String, String> parameters = new TreeMap<>();
        if (activeOnly != null) parameters.put("activeOnly", String.valueOf(activeOnly));
        return request("user.ratedList", parameters, User[].class);
    }

    /**
     * Returns rating history of the specified user.
     *
     * @param handle Codeforces user handle.
     * @return A list of RatingChange objects for requested user.
     * @throws InterruptedException When the thread is interrupted during the request.
     * @throws CFException          When the Codeforces API responses with a failure.
     * @throws IOException          When the HTTP API connection fails.
     */
    public RatingChange[] requestUserRating(String handle) throws InterruptedException, CFException, IOException {
        if (handle == null) throw new NullPointerException("handle is null!");
        SortedMap<String, String> parameters = new TreeMap<>();
        parameters.put("handle", handle);
        return request("user.rating", parameters, RatingChange[].class);
    }

    /**
     * Returns submissions of specified user.
     *
     * @param handle Codeforces user handle.
     * @param from   (optional) (can be null) 1-based index of the first submission to return.
     * @param count  (optional) (can be null) Number of returned submissions.
     * @return A list of Submission objects, sorted in decreasing order of submission id.
     * @throws InterruptedException When the thread is interrupted during the request.
     * @throws CFException          When the Codeforces API responses with a failure.
     * @throws IOException          When the HTTP API connection fails.
     */
    public Submission[] requestUserStatus(String handle, Integer from, Integer count) throws InterruptedException, CFException, IOException {
        if (handle == null) throw new NullPointerException("handle is null!");
        SortedMap<String, String> parameters = new TreeMap<>();
        parameters.put("handle", handle);
        if (from != null) parameters.put("from", String.valueOf(from));
        if (count != null) parameters.put("count", String.valueOf(count));
        return request("user.status", parameters, Submission[].class);
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
        private String lang = "en";
        private String apiKey;
        private String apiSecret;

        private Builder() {
        }

        /**
         * Sets the language for Language-depended fields like names or descriptions.
         *
         * @param lang The language to use, can be <b>en</b> or <b>ru</b>.
         * @return This builder.
         */
        public Builder language(String lang) {
            this.lang = lang;
            return this;
        }

        /**
         * Resets to the default language (en).
         *
         * @return This builder.
         */
        public Builder language() {
            this.lang = "en";
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
            return new CodeforcesClient(httpClientBuilder.build(), lang, apiKey, apiSecret);
        }
    }

    /**
     * An API request result.
     *
     * @param <T> The type of the result object.
     */
    private static class Result<T> {
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
        @SuppressWarnings("unused")
        private enum Status {
            OK, FAILED
        }
    }
}
