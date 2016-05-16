package com.mpush.client;

import com.mpush.api.Client;
import com.mpush.api.ClientListener;
import com.mpush.util.DefaultLogger;

import java.util.concurrent.locks.LockSupport;

/**
 * Created by ohun on 2016/1/25.
 */
public class MPushClientTest {
    private static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCghPCWCobG8nTD24juwSVataW7iViRxcTkey/B792VZEhuHjQvA3cAJgx2Lv8GnX8NIoShZtoCg3Cx6ecs+VEPD2fBcg2L4JK7xldGpOJ3ONEAyVsLOttXZtNXvyDZRijiErQALMTorcgi79M5uVX9/jMv2Ggb2XAeZhlLD28fHwIDAQAB";
    private static final String allocServer = "http://127.0.0.1:9999/";

    public static void main(String[] args) throws Exception {
        Client client = ClientConfig
                .build()
                .setPublicKey(publicKey)
                .setAllotServer(allocServer)
                .setDeviceId("1111111111")
                .setOsName("Android")
                .setOsVersion("6.0")
                .setClientVersion("2.0")
                .setUserId("doctor43test")
                .setSessionStorageDir(MPushClientTest.class.getResource("/").getFile())
                .setLogger(new DefaultLogger())
                .setLogEnabled(true)
                .setEnableHttpProxy(false)
                .setClientListener(new L())
                .create();
        client.start();

        LockSupport.park();
    }




    public static class L implements ClientListener {
        Thread thread;
        boolean flag = true;

        @Override
        public void onConnected(Client client) {
            flag = true;
        }

        @Override
        public void onDisConnected(Client client) {
            flag = false;
        }

        @Override
        public void onHandshakeOk(final Client client, final int heartbeat) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (flag && client.isRunning()) {
                        try {
                            Thread.sleep(heartbeat);
                        } catch (InterruptedException e) {
                            break;
                        }
                        client.healthCheck();
                    }
                }
            });
            thread.start();
        }

        @Override
        public void onReceivePush(Client client, String content) {

        }

        @Override
        public void onKickUser(String deviceId, String userId) {

        }

    }
}