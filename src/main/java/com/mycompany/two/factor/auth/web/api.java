package com.mycompany.two.factor.auth.web;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import de.taimos.totp.TOTP;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import java.util.Base64;
import java.util.Scanner;
import javax.ws.rs.Produces;

import org.apache.commons.io.FileUtils;

/**
 * @author jackw
 */
@Path("/auth")
public class api {

    private static final String SAVEBASELOCATION = "C:\\Users\\jackw\\Desktop\\QR\\";

    /**
     * @return @throws Exceptions
     */
    @GET
    @Path("/login/{username}/{password}")
    @Consumes(MediaType.APPLICATION_JSON)
    @PermitAll
    public String login(@PathParam("username") String username, @PathParam("password") String password) throws Exception {

        try {
            File myObj = new File(SAVEBASELOCATION + "myfile.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {

                String nextLine = myReader.nextLine();

                String[] arr = nextLine.split("-");

                String name = arr[1];
                String pass = arr[2];
                if (name.equals(username) && pass.equals(password)) {
                    return "PASS";
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return "FAIL";
    }

    /**
     * @param username
     * @param authCode
     * @return
     * @throws WriterException
     */
    @GET
    @Path("/authenticate/{username}/{authcode}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String authenticate(@PathParam("username") String username, @PathParam("authcode") String authCode) throws WriterException {

        String secretKey = "";
        try {
            File myObj = new File(SAVEBASELOCATION + "myfile.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {

                String[] arr = myReader.nextLine().split("-");

                String name = arr[1];

                if (name.equals(username)) {
                    secretKey = arr[0];
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (authCode.equals(api.getTOTPCode(secretKey))) {
            return "PASS";
        }

        return "FAIL";
    }

    /**
     * @return
     */
    private static String convertImageToBase64(String filePath) throws IOException {
        byte[] fileContent = FileUtils.readFileToByteArray(new File(filePath));
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        return encodedString;
    }

    @GET
    @Path("/createauthdetails/{username}/{password}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String getGoogleAuthDetails(@PathParam("username") String username, @PathParam("password") String password) throws WriterException, IOException {

        String secretKey = generateSecretKey();
        String barCodeUrl = api.getGoogleAuthenticatorBarCode(secretKey, username, "JEM");
        api.createQRCode(barCodeUrl, SAVEBASELOCATION + "qr.png", 200, 200);
        String encodedString = "data:image/png;base64," + convertImageToBase64(SAVEBASELOCATION + "qr.png");

        api.write(secretKey + "-" + username + "-" + password);
        return secretKey + "-" + encodedString;
    }

    /**
     * @param str
     * @throws IOException
     */
    private static void write(final String str) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(SAVEBASELOCATION + "myfile.txt", true)));
        out.println(str);
        out.close();
    }

    /**
     * Generates 20-byte key for Google Authenticator (required for new entry).
     * SIHGL7J3UE5ZNGGBPINBXSZAGWTCH6RY
     *
     * @return
     */
    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    /**
     * Converts secret 20-byte key to hexadecimal and uses TOPT algorithm to
     * turn the hex code into a 6-digit code based on time. So, we use the
     * hexadecimal value of the secret key plus the current time to produce a
     * 6-digit code that can be used to authenticate.
     *
     * @param secretKey
     * @return
     */
    public static String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

    /**
     * Returns String used to generate QR code that user can authenticate with.
     *
     * @param secretKey - 20-byte key associated with the account ID in Google
     * Authenticator
     * @param account - user ID for Google Authenticator (usually email address
     * but can be anything)
     * @param issuer - company name
     * @return
     */
    public static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Generates QR code from bar code String.
     *
     * @param barCodeData
     * @param filePath
     * @param height
     * @param width
     * @throws WriterException
     */
    public static void createQRCode(String barCodeData, String filePath, int height, int width) throws WriterException {
        BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, width, height);
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
