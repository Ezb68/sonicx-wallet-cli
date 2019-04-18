package org.sonicx.demo;

import org.sonicx.api.GrpcAPI.EasyTransferResponse;
import org.sonicx.common.utils.ByteArray;
import org.sonicx.common.utils.Utils;
import org.sonicx.protos.Protocol.Transaction;
import org.sonicx.walletserver.WalletApi;

public class EasyTransferAssetByPrivateDemo {

  public static void main(String[] args) {
    String privateKey = "D95611A9AF2A2A45359106222ED1AFED48853D9A44DEFF8DC7913F5CBA727366";
    String toAddress = "TKMZBoWbXbYedcBnQugYT7DaFnSgi9qg78";
    String tokenId = "1000001";
    EasyTransferResponse response = WalletApi
        .easyTransferAssetByPrivate(ByteArray.fromHexString(privateKey),
            WalletApi.decodeFromBase58Check(toAddress), tokenId, 1000000L);

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
