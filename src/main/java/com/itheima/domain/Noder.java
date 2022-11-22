package com.itheima.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Noder {
    private int ID;
    private double data;
    private double LOC_X;
    private double LOC_Y;
    private double LOC_Z;
    private double STRESS_X;
    private double STRESS_Y;
    private double STRESS_Z;
    private double STRESS_XY;
    private double STRESS_XZ;
    private double STRESS_YZ;
    private double DEFORMATION_X;
    private double DEFORMATION_Y;
    private double DEFORMATION_Z;
    private double STRAIN_X;
    private double STRAIN_Y;
    private double STRAIN_Z;
    private double STRAIN_XY;
    private double STRAIN_XZ;
    private double STRAIN_YZ;
    private double U_X;
    private double U_Y;
    private double U_Z;
}
