/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jcrete.tinkerforge;

/**
 *
 * @author sven
 */
public class Brick {
    private final String uid;
    private final String connectedUid;
    private final char position;
    private final short[] hardwareVersion;
    private final short[] firmwareVersion;
    private final int deviceIdentifier;
    private final short enumerationType;
    public Brick(String uid, String connectedUid, char position,
                short[] hardwareVersion, short[] firmwareVersion,
                int deviceIdentifier, short enumerationType) {
        this.uid = uid;
        this.connectedUid = connectedUid;
        this.position = position;
        this.hardwareVersion = hardwareVersion;
        this.firmwareVersion = firmwareVersion;
        this.deviceIdentifier = deviceIdentifier;
        this.enumerationType = enumerationType;
        
    }
    
    public String getName() {
        return BrickType.getType(deviceIdentifier).getName();
    }
    
    public String getDisplayName() {
        return getName() + "[" + uid + "]";
    }
    
    public String toString() {
        return getDisplayName();
    }
}
