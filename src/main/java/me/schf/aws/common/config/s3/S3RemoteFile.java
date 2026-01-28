package me.schf.aws.common.config.s3;

public class S3RemoteFile implements RemoteFile {

	private String bucket;
	private String key;

	public S3RemoteFile() {
		super();
	}

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

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
