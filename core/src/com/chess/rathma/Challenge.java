package com.chess.rathma;

import com.chess.rathma.Packets.ChallengePacket;

/**
 * Small container to rebuild challenge text items off of.
 */
public class Challenge {
    public Challenge(String username, int challengeID)
    {
        this.username = username;
        this.challengeID = challengeID;

    }
    public Challenge(ChallengePacket packet)
    {

    }
    public String username;
    public int challengeID;
}
