package me.schf.aws.common.config.s3;

@FunctionalInterface
public interface RemoteFileRetriever<T extends RemoteFile> {

	byte[] getFile(T t);

}
