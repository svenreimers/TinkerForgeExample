/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jcrete.tinkerforge;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 * @author sven
 */
public enum BrickType {
    DC_BRICK(11, "DC Brick"),
    MASTER_BRICK(13, "Master Brick"),
    SERVO_BRICK(14, "Servo Brick"),
    STEPPER_BRICK(15, "Stepper Brick"),
    IMU_BRICK(16, "IMU Brick"),
    RED_BRICK(17, "RED Brick"),
    IMU_BRICK_20(18, "IMU Brick 2.0"),
    AMBIENT_LIGHT_BRICKLET(21, "Ambient Light Bricklet"),
    CURRENT12_BRICKLET(23, "Current12 Bricklet"),
    CURRENT25_BRICKLET(24, "Current25 Bricklet")
    ;

    private int id;
    private String name;
    
    BrickType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public static BrickType getType(int identifier) {
        Optional<BrickType> brickType = Stream.of(BrickType.values()).filter((type) -> type.id == identifier).findFirst();
        if (brickType.isPresent()) {
            return brickType.get();
        } else {
            throw new IllegalArgumentException("Unknow Brick Type " + identifier);
        }
    }
    
}
