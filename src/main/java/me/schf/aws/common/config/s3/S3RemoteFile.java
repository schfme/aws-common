package me.schf.aws.common.config.s3;

public class S3RemoteFile implements RemoteFile {

	private final String bucket;
	private final String key;

	public S3RemoteFile(String bucket, String key) {
		super();
		this.bucket = bucket;
		this.key = key;
	}

	@Override
	public String identifier() {
		return "s3://%s/%s".formatted(bucket, key);
	}

	public String getBucket() {
		return bucket;
	}

	public String getKey() {
		return key;
	}

}
