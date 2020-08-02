package com.github.rami_sabbagh.codeforces.api;

import com.github.rami_sabbagh.codeforces.api.objects.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Codeforces {

    /**
     * The base URL of the Codeforces API, override it if ever needed.
     */
    public final String baseURL = "https://codeforces.com/api/";
    /**
     * Requests timeout duration.
     */
    public Duration timeout = Duration.ofSeconds(10);
    /**
     * The api key for authorization, null when disabled.
     */
    protected String apiKey;
    /**
     * The api secret for authorization, null when disabled.
     */
    protected String apiSecret;
    /**
     * The random generator for the authorization string, seed initialized into the system time.
     */
    protected Random randomGenerator = new Random(System.currentTimeMillis());
    /**
     * The gson instance used for deserializing the responses (from JSON).
     */
    protected Gson gson = new Gson();

    public Codeforces() {
    }

    public Codeforces(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    /**
     * Calculate SHA-512 of a string.
     * Sourced from https://www.geeksforgeeks.org/sha-512-hash-in-java/.
     *
     * @param input The input string to hash.
     * @return The SHA-512 hash in HEX format.
     */
    public static String calculateSHA512(String input) {
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
     * Returns information about one or several users.
     *
     * @param handles Semicolon-separated list of handles. No more than 10000 handles is accepted.
     * @return Returns a list of User objects for requested handles.
     */
    public User[] requestUsersInformation(String handles) throws IOException, InterruptedException, CFException {
        String response = request("user.info", "handles=" + handles);

        Type resultType = new TypeToken<Result<User[]>>() {
        }.getType();
        Result<User[]> result = gson.fromJson(response, resultType);

        if (result.status == Result.Status.FAILED)
            throw new CFException(result.comment);

        return result.result;
    }

    /**
     * Execute a HTTP request on CodeForces's API.
     *
     * @param methodName The name of the API method to execute.
     * @param parameters The request's parameters url encoded.
     * @return The response body.
     * @throws IOException          by HTTP operations.
     * @throws InterruptedException when interrupted while requesting
     */
    public String request(String methodName, String parameters) throws IOException, InterruptedException {
        String endpoint = (apiKey == null) ? (methodName + "?" + parameters) : authorize(methodName, parameters);
        URI requestURL = URI.create(baseURL + endpoint);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(requestURL).timeout(timeout).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    /**
     * Generates a 6 characters string of random characters for usage in authorization.
     *
     * @return A 6 characters string of random characters.
     */
    protected String generateRandomString() {
        //The characters we're going to pick from.
        char[] charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

        //Generate 6 random characters.
        char[] result = new char[6];
        for (int i = 0; i < 6; i++)
            result[i] = charset[Math.abs(randomGenerator.nextInt() % charset.length)];

        //Convert them into a string.
        return String.valueOf(result);
    }

    /**
     * Calculates the authorized endpoint for a CodeForces request.
     * Reference: https://codeforces.com/apiHelp
     *
     * @param methodName       The request's method name.
     * @param packedParameters The parameters of the request, can be an empty string.
     * @return The authorized endpoint with parameters embedded, valid for about 5 minutes.
     */
    protected String authorize(String methodName, String packedParameters) {
        //The request parameters
        ArrayList<String> parameters = new ArrayList<>(Arrays.asList(packedParameters.split("&")));

        //The timestamp for the authorization.
        String time = String.valueOf(System.currentTimeMillis() / 1000);

        //Add the apiKey and time parameters.
        parameters.add("apiKey=" + apiKey);
        parameters.add("time=" + time);

        //Sort the parameters lexicographically.
        Collections.sort(parameters);

        //Pack the parameters into a string again.
        packedParameters = String.join("&", parameters);

        //The random string for the apiSig.
        String rand = generateRandomString();

        //The base string for apiSig.
        String hashString = String.format("%s/%s?%s#%s", rand, methodName, packedParameters, apiSecret);

        //The calculated apiSig.
        String apiSig = rand + calculateSHA512(hashString);

        //Format the authorized request endpoint.
        return String.format("%s?%s&apiSig=%s", methodName, packedParameters, apiSig);
    }

    /**
     * The API request result.
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
