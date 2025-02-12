package net.minecraft.client.multiplayer.resolver;

import java.net.InetSocketAddress;

public interface ResolvedServerAddress {
    String getHostName();

    String getHostIp();

    int getPort();

    InetSocketAddress asInetSocketAddress();

    static ResolvedServerAddress from(final InetSocketAddress pInetSocketAddress) {
        return new ResolvedServerAddress() {
            @Override
            public String getHostName() {
                return pInetSocketAddress.getAddress().getHostName();
            }

            @Override
            public String getHostIp() {
                return pInetSocketAddress.getAddress().getHostAddress();
            }

            @Override
            public int getPort() {
                return pInetSocketAddress.getPort();
            }

            @Override
            public InetSocketAddress asInetSocketAddress() {
                return pInetSocketAddress;
            }
        };
    }
}