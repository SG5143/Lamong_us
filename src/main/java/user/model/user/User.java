package user.model.user;

import java.sql.*;

import org.mindrot.jbcrypt.*;

public class User {

	private String uuid;
	private String username;
	private String password;
	private String nickname;
	private String phone;
	private String email;
	private Boolean deleteStatus;
	private String loginType;
	private Boolean banStatus;
	private String profileInfo;
	private byte[] profileImage;
	private int score;
	private String apiKey;
	private Timestamp regDate;
	private Timestamp modDate;

	public User(String uuid, String username, String password, String nickname, String phone, String email,
			Boolean deleteStatus, String loginType, Boolean banStatus, String profileInfo, byte[] profileImage,
			int score, String apiKey, Timestamp regDate, Timestamp modDate) {
		this.uuid = uuid;
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.phone = phone;
		this.email = email;
		this.deleteStatus = deleteStatus;
		this.loginType = loginType;
		this.banStatus = banStatus;
		this.profileInfo = profileInfo;
		this.profileImage = profileImage;
		this.score = score;
		this.apiKey = apiKey;
		this.regDate = regDate;
		this.modDate = modDate;
	}

	public User(String uuid, String nickname, String profileInfo, byte[] profileImage, int score, Timestamp regDate) {
		this.uuid = uuid;
		this.nickname = nickname;
		this.profileInfo = profileInfo;
		this.profileImage = profileImage;
		this.score = score;
		this.regDate = regDate;
	}

	public String getUuid() {
		return uuid;
	}

	public String getUsername() {
		return username;
	}

	public boolean checkPassword(String password) {
		boolean isChecked = false;

		try {
			isChecked = BCrypt.checkpw(password, this.password);
		} catch (Exception e) {
			System.err.println("암호화되지 않은 값이 저장되어 있습니다.");
		}
		return isChecked;
	}

	public String getPassword() {
		return password;
	}

	public String getNickname() {
		return nickname;
	}

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	public Boolean getDeleteStatus() {
		return deleteStatus;
	}

	public String getLoginType() {
		return loginType;
	}

	public Boolean getBanStatus() {
		return banStatus;
	}

	public void setBanStatus(Boolean banStatus) {
		this.banStatus = banStatus;
	}

	public String getProfileInfo() {
		return profileInfo;
	}

	public byte[] getProfileImage() {
		return profileImage;
	}

	public int getScore() {
		return score;
	}

	public String getApiKey() {
		return apiKey;
	}

	public Timestamp getRegDate() {
		return regDate;
	}

	public Timestamp getModDate() {
		return modDate;
	}

	@Override
	public String toString() {
		return String.format(
				"UUID: %s\nUsername: %s\nPassword: %s\nNickname: %s\nPhone: %s\nEmail: %s\n"
						+ "Delete Status: %b\nLogin Type: %s\nBan Status: %b\nProfile Info: %s\n"
						+ "Score: %d\nAPI Key: %s\nReg Date: %s\nMod Date: %s",
				uuid, username, password, nickname, phone, email, deleteStatus, loginType, banStatus, profileInfo,
				score, apiKey, regDate, modDate);
	}

}
