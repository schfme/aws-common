package me.schf.aws.common.config.s3;

@FunctionalInterface
public interface RemoteFileWriter<T extends RemoteFile> {

	void write(T t, byte[] contents);

}