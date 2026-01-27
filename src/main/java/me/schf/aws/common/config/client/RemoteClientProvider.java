package me.schf.aws.common.config.client;

@FunctionalInterface
public interface RemoteClientProvider<T> {

	T getClient();

}