package user.model.user;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

public class UserRequestDto {
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

	public UserRequestDto() {
	}

	public UserRequestDto(String username, String password, String nickname, String phone, String email,
			String loginType) {
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.phone = phone;
		this.email = email;
		this.loginType = loginType;
		this.profileImage = getRandomProfileImage();
	}

	private byte[] getRandomProfileImage() {
		try {
			String imagePath = "webapp/resources/images/";

			String[] imageFiles = new String[] { "Default01.jpg", "Default02.jpg", "Default03.jpg", "Default04.jpg",
					"Default05.jpg", "Default06.jpg", "Default07.jpg", "Default08.jpg", "Default09.jpg",
					"Default10.jpg", "Default11.jpg" };

			Random random = new Random();
			int index = random.nextInt(imageFiles.length);

			File imageFile = new File(imagePath + imageFiles[index]);

			if (imageFile.exists())
				return Files.readAllBytes(imageFile.toPath());
			else
				throw new IOException("Default image not found");

		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0];
		}
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

}
