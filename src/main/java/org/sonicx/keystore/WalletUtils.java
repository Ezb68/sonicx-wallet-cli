package org.sonicx.keystore;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.sonicx.common.crypto.ECKey;
import org.sonicx.common.utils.Utils;
import org.sonicx.core.exception.CipherException;

/**
 * Utility functions for working with Wallet files.
 */
public class WalletUtils {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  static {
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public static String generateFullNewWalletFile(byte[] password, File destinationDirectory)
      throws NoSuchAlgorithmException, NoSuchProviderException,
      InvalidAlgorithmParameterException, CipherException, IOException {

    return generateNewWalletFile(password, destinationDirectory, true);
  }

  public static String generateLightNewWalletFile(byte[] password, File destinationDirectory)
      throws NoSuchAlgorithmException, NoSuchProviderException,
      InvalidAlgorithmParameterException, CipherException, IOException {

    return generateNewWalletFile(password, destinationDirectory, false);
  }

  public static String generateNewWalletFile(
      byte[] password, File destinationDirectory, boolean useFullScrypt)
      throws CipherException, IOException, InvalidAlgorithmParameterException,
      NoSuchAlgorithmException, NoSuchProviderException {

    ECKey ecKeyPair = new ECKey(Utils.getRandom());
    return generateWalletFile(password, ecKeyPair, destinationDirectory, useFullScrypt);
  }

  public static String generateWalletFile(
      byte[] password, ECKey ecKeyPair, File destinationDirectory, boolean useFullScrypt)
      throws CipherException, IOException {

    WalletFile walletFile;
    if (useFullScrypt) {
      walletFile = Wallet.createStandard(password, ecKeyPair);
    } else {
      walletFile = Wallet.createLight(password, ecKeyPair);
    }

    String fileName = getWalletFileName(walletFile);
    File destination = new File(destinationDirectory, fileName);

    objectMapper.writeValue(destination, walletFile);

    return fileName;
  }

  public static void updateWalletFile(
      byte[] password, ECKey ecKeyPair, File source, boolean useFullScrypt)
      throws CipherException, IOException {

    WalletFile walletFile = objectMapper.readValue(source, WalletFile.class);
    if (useFullScrypt) {
      walletFile = Wallet.createStandard(password, ecKeyPair);
    } else {
      walletFile = Wallet.createLight(password, ecKeyPair);
    }

    objectMapper.writeValue(source, walletFile);
  }

  public static String generateWalletFile(WalletFile walletFile, File destinationDirectory)
      throws IOException {
    String fileName = getWalletFileName(walletFile);
    File destination = new File(destinationDirectory, fileName);

    objectMapper.writeValue(destination, walletFile);
    return fileName;
  }

  //    /**
//     * Generates a BIP-39 compatible Ethereum wallet. The private key for the wallet can
//     * be calculated using following algorithm:
//     * <pre>
//     *     Key = SHA-256(BIP_39_SEED(mnemonic, password))
//     * </pre>
//     *
//     * @param password Will be used for both wallet encryption and passphrase for BIP-39 seed
//     * @param destinationDirectory The directory containing the wallet
//     * @return A BIP-39 compatible Ethereum wallet
//     * @throws CipherException if the underlying cipher is not available
//     * @throws IOException if the destination cannot be written to
//     */
//    public static Bip39Wallet generateBip39Wallet(String password, File destinationDirectory)
//            throws CipherException, IOException {
//        byte[] initialEntropy = new byte[16];
//        secureRandom.nextBytes(initialEntropy);
//
//        String mnemonic = MnemonicUtils.generateMnemonic(initialEntropy);
//        byte[] seed = MnemonicUtils.generateSeed(mnemonic, password);
//        ECKeyPair privateKey = ECKeyPair.create(sha256(seed));
//
//        String walletFile = generateWalletFile(password, privateKey, destinationDirectory, false);
//
//        return new Bip39Wallet(walletFile, mnemonic);
//    }
//
//    public static Credentials loadCredentials(String password, String source)
//            throws IOException, CipherException {
//        return loadCredentials(password, new File(source));
//    }
//
  public static Credentials loadCredentials(byte[] password, File source)
      throws IOException, CipherException {
    WalletFile walletFile = objectMapper.readValue(source, WalletFile.class);
    return Credentials.create(Wallet.decrypt(password, walletFile));
  }

  public static WalletFile loadWalletFile(File source) throws IOException {
   return objectMapper.readValue(source, WalletFile.class);
  }
//
//    public static Credentials loadBip39Credentials(String password, String mnemonic) {
//        byte[] seed = MnemonicUtils.generateSeed(mnemonic, password);
//        return Credentials.create(ECKeyPair.create(sha256(seed)));
//    }

  private static String getWalletFileName(WalletFile walletFile) {
    DateTimeFormatter format = DateTimeFormatter.ofPattern(
        "'UTC--'yyyy-MM-dd'T'HH-mm-ss.nVV'--'");
    ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);

    return now.format(format) + walletFile.getAddress() + ".json";
  }

  public static String getDefaultKeyDirectory() {
    return getDefaultKeyDirectory(System.getProperty("os.name"));
  }

  static String getDefaultKeyDirectory(String osName1) {
    String osName = osName1.toLowerCase();

    if (osName.startsWith("mac")) {
      return String.format(
          "%s%sLibrary%sEthereum", System.getProperty("user.home"), File.separator,
          File.separator);
    } else if (osName.startsWith("win")) {
      return String.format("%s%sEthereum", System.getenv("APPDATA"), File.separator);
    } else {
      return String.format("%s%s.ethereum", System.getProperty("user.home"), File.separator);
    }
  }

  public static String getTestnetKeyDirectory() {
    return String.format(
        "%s%stestnet%skeystore", getDefaultKeyDirectory(), File.separator, File.separator);
  }

  public static String getMainnetKeyDirectory() {
    return String.format("%s%skeystore", getDefaultKeyDirectory(), File.separator);
  }

//    public static boolean isValidPrivateKey(String privateKey) {
//        String cleanPrivateKey = Numeric.cleanHexPrefix(privateKey);
//        return cleanPrivateKey.length() == PRIVATE_KEY_LENGTH_IN_HEX;
//    }
//
//    public static boolean isValidAddress(String input) {
//        String cleanInput = Numeric.cleanHexPrefix(input);
//
//        try {
//            Numeric.toBigIntNoPrefix(cleanInput);
//        } catch (NumberFormatException e) {
//            return false;
//        }
//
//        return cleanInput.length() == ADDRESS_LENGTH_IN_HEX;
//    }
}
