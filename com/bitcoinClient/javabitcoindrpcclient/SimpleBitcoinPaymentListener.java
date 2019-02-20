/*
 * Copyright (c) 2013, Mikhail Yevchenko. All rights reserved. PROPRIETARY/CONFIDENTIAL.
 */
package com.bitcoinClient.javabitcoindrpcclient;

import com.bitcoinClient.javabitcoindrpcclient.BitcoindRpcClient.Transaction;

public class SimpleBitcoinPaymentListener implements BitcoinPaymentListener {

  @Override
  public void block(String blockHash) {
  }

  @Override
  public void transaction(Transaction transaction) {
  }

}
