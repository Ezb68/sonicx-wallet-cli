package org.sonicx.demo;

import java.util.Arrays;
import org.sonicx.api.GrpcAPI.EasyTransferResponse;
import org.sonicx.common.crypto.ECKey;
import org.sonicx.common.crypto.Sha256Hash;
import org.sonicx.common.utils.ByteArray;
import org.sonicx.common.utils.Utils;
import org.sonicx.protos.Protocol.Transaction;
import org.sonicx.walletserver.WalletApi;

public class EasyTransferAssetDemo {

  private static byte[] getAddressByPassphrase(String passPhrase) {
    byte[] privateKey = Sha256Hash.hash(passPhrase.getBytes());
    ECKey ecKey = ECKey.fromPrivate(privateKey);
    byte[] address = ecKey.getAddress();
    return address;
  }

  public static void main(String[] args) {
    String passPhrase = "test pass phrase";
    byte[] address = WalletApi.createAdresss(passPhrase.getBytes());
    String tokenId = "1000001";
    if (!Arrays.equals(address, getAddressByPassphrase(passPhrase))) {
      System.out.println("The address is diffrent !!");
    }
    System.out.println("address === " + WalletApi.encode58Check(address));

    EasyTransferResponse response = WalletApi
        .easyTransferAsset(passPhrase.getBytes(), getAddressByPassphrase("test pass phrase 2"),
            tokenId, 10000L);
    if (response.getResult().getResult() == true) {
      Transaction transaction = response.getTransaction();
      System.out.println("Easy transfer successful!!!");
      System.out.println(
          "Receive txid = " + ByteArray.toHexString(response.getTxid().toByteArray()));
      System.out.println(Utils.printTransaction(transaction));
    } else {
      System.out.println("Easy transfer failed!!!");
      System.out.println("Code = " + response.getResult().getCode());
      System.out.println("Message = " + response.getResult().getMessage().toStringUtf8());
    }
  }
}
