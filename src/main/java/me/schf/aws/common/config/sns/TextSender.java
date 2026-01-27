package me.schf.aws.common.config.sns;

public interface TextSender {

	void sendText(String phoneNumber, String message);

}
