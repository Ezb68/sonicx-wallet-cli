# sonicx-wallet-cli [![Build Status](https://travis-ci.org/SonicXChain/sonicx-wallet-cli.svg?branch=master)](https://travis-ci.org/SonicXChain/sonicx-wallet-cli)
SonicX Wallet CLI


Download sonicx-wallet-cli
---------------------------------
git clone https://github.com/SonicXChain/sonicx-wallet-cli.git


Edit config.conf in src/main/resources
----------------------------------------
```
net {
 type = mainnet
 #type = testnet 
}

fullnode = {
  ip.list = [
    "fullnode ip : port"
  ]
}

soliditynode = {
  ip.list = [
    "solidity ip : port"
  ]
}//note: solidity node is optional

```
Build and run sonicx-wallet-cli by command line
----------------------------------------
Create a new command line terminal window.

```
cd sonicx-wallet-cli  
./gradlew build      
./gradlew run
```

Build and run web wallet
----------------------------------------
```
cd sonicx-wallet-cli  
./gradlew build
cd build/libs
java -jar sonicx-wallet-cli.jar
```


How sonicx-wallet-cli connects to sonicx :
--------------------------------------
sonicx-wallet-cli connect to sonicx by grpc protocol.          
sonicx nodes can be deployed locally or remotely.          
We can set the connected sonicx node IP in config.conf of sonicx-wallet-cli.
 

sonicx-wallet-cli supported command list:
----------------------------------

RegisterWallet  
RegisterWallet Password
Register a wallet in local.
Generate a pair of ecc keys.
Derive a AES Key by password and then use the AES algorithm to encrypt and save the private key.
The account address is calculated by the public key sha3-256, and taking the last 20 bytes.
All subsequent operations that require the use of a private key must enter the password.

ImportWallet  
ImportWalletByBase64  
ChangePassword  
Login  
Logout  
BackupWallet  
BackupWallet2Base64  
GetAddress  
GetBalance  
GetAccount  
GetAssetIssueByAccount                          
GetAssetIssueByName                       
SendCoin  
TransferAsset  
ParticipateAssetIssue  
AssetIssue  
CreateWitness  
VoteWitness  
FreezeBalance
UnfreezeBalance
WithdrawBalance
ListAccounts  
ListWitnesses  
ListAssetIssue    
listNodes               
GetAssetIssueByName   
GetBlock
UpdateAccount  
Exit or Quit  
help  

Input any one of them, you will get more tips.


How to freeze/unfreeze balance
----------------------------------

After the funds are frozen, the corresponding number of shares and bandwidth will be obtained.
 Shares can be used for voting and bandwidth can be used for trading.
 The rules for the use and calculation of share and bandwidth are described later in this article.


**Freeze operation is as follows：**

```
freezeBalance amount time energy/bandwidth address
```

*amount:The amount of frozen funds，the unit is drop.
The minimum value is **1000000 drop(1SOX)**.*

*time：Freeze time, this value is currently only allowed for **3 days***


For example：
```
freezeBalance 100000000 3 1 address
```


After the freeze operation,frozen funds will be transferred from Account Balance to Frozen,
You can view frozen funds from your account information.
After being unfrozen, it is transferred back to Balance by Frozen, and the frozen funds cannot be used for trading.


When more share or bandwidth is needed temporarily, additional funds may be frozen to obtain additional share and bandwidth.
The unfrozen time is postponed until 3 days after the last freeze operation

After the freezing time expires, funds can be unfroze.


**Unfreeze operation is as follows：**
```
unfreezebalance password 
```



How to vote
----------------------------------

Voting requires share. Share can be obtained by freezing funds.

- The share calculation method is: **1** unit of share can be obtained for every **1SOX** frozen. 
- After unfreezing, previous vote will expire. You can avoid the invalidation of the vote by re-freezing and voting.

**Note:** The SonicX Network only records the status of your last vote, which means that each of your votes will cover all previous voting results.

For example：

```
freezeBalance 100000000 3 1 address  // Freeze 10SOX and acquire 10 units of shares

votewitness 123455 witness1 4 witness2 6   // Cast 4 votes for witness1 and 6 votes for witness2 at the same time.

votewitness 123455 witness1 10   // Voted 10 votes for witness1.
```

The final result of the above command was 10 votes for witness1 and 0 votes for witness2.



How to calculate bandwidth
----------------------------------

The bandwidth calculation rule is：
```
constant * FrozenFunds * days
```
Assuming freeze 1SOX（1_000_000 DROP），3 days，bandwidth obtained = 1* 1_000_000 * 3 = 3_000_000. 

Any contract needs to consume bandwidth, including transfer, transfer of assets, voting, freezing, etc. 
The query does not consume bandwidth, and each contract needs to consume **100_000 bandwidth**. 

If the previous contract exceeds a certain time (**10s**), this operation does not consume bandwidth. 

When the unfreezing operation occurs, the bandwidth is not cleared. 
The next time the freeze is performed, the newly added bandwidth is accumulated.


How to withdraw balance
----------------------------------

After each block is produced, the block award is sent to the account's allowance, 
and an withdraw operation is allowed every **24 hours** from allowance to balance. 
The funds in allowance cannot be locked or traded.
 

How to create witness
----------------------------------
Applying to become a witness account needs to consume **100_000SOX**.
This part of the funds will be burned directly.


How to create account
----------------------------------
It is not allowed to create accounts directly. You can only create accounts by transferring funds to non-existing accounts.
Transfer to a non-existent account with a minimum transfer amount of **1SOX**.

Command line operation flow example
-----------------------------------      

cd sonicx-wallet-cli  
./gradlew build      
./gradlew run                                                                               
RegisterWallet 123456      (password = 123456)                                                        
login 123456                                                                                           
getAddress                 (Print 'address = f286522619d962e6f93235ca27b2cb67a9e5c27b', backup it)                                                       
BackupWallet 123456        (Print 'priKey = 22be575f19b9ac6e94c7646a19a4c89e06fe99e2c054bd242c0af2b6282a65e9', backup it) (BackupWallet2Base64 option)                                                    
getbalance                 (Print 'Balance = 0')                                                                                                                                          
 
getbalance                                                             
          
assetIssue 123456 testAssetIssue00001 10000000000000000 1 100 2018-4-1 2018-4-30 1 just-test https://github.com/SonicXChain/sonicx-wallet-cli/                   
getaccount  f286522619d962e6f93235ca27b2cb67a9e5c27b                                                                        
(Print balance: 9999900000                                                                          
asset {                                                                                                     
  key: "testAssetIssue00001"                                                                           
  value: 10000000000000000                                                                             
})                                                                                                       
(cost 1000 sox for assetIssue)                                                                    
(You can query the sox balance and other asset balances for any account )                                                
TransferAsset 123456 649DDB4AB82D558AD6809C7AB2BA43D1D1054B3F testAssetIssue00001 10000                                                     
