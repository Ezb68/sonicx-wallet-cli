package org.sonicx.core.config;

public interface Parameter {

  interface CommonConstant {
    byte ADD_PRE_FIX_BYTE_MAINNET = (byte) 0x3f;   //3f + address
    byte ADD_PRE_FIX_BYTE_TESTNET = (byte) 0xa0;   //a0 + address
    int ADDRESS_SIZE = 21;
  }

}
