package user.model.block;

import java.sql.Timestamp;

public class Block {
    private String blockingUser; 
    private String blockedUser; 
    private Timestamp regDate;  

    public Block() {
    }

    public Block(String blockingUser, String blockedUser, Timestamp regDate) {
        this.blockingUser = blockingUser;
        this.blockedUser = blockedUser;
        this.regDate = regDate;
    }

    public String getBlockingUser() {
        return blockingUser;
    }

    public void setBlockingUser(String blockingUser) {
        this.blockingUser = blockingUser;
    }

    public String getBlockedUser() {
        return blockedUser;
    }

    public void setBlockedUser(String blockedUser) {
        this.blockedUser = blockedUser;
    }

    public Timestamp getRegDate() {
        return regDate;
    }

    public void setRegDate(Timestamp regDate) {
        this.regDate = regDate;
    }
}
