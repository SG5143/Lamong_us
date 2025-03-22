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

	public User() {
	}

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

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(Boolean deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
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

	public void setProfileInfo(String profileInfo) {
		this.profileInfo = profileInfo;
	}

	public byte[] getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(byte[] profileImage) {
		this.profileImage = profileImage;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public Timestamp getRegDate() {
		return regDate;
	}

	public void setRegDate(Timestamp regDate) {
		this.regDate = regDate;
	}

	public Timestamp getModDate() {
		return modDate;
	}

	public void setModDate(Timestamp modDate) {
		this.modDate = modDate;
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
