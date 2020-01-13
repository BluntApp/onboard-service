package com.blunt.onboard.type;

import lombok.Getter;

@Getter
public enum Mode {
  MOBILE("MOBILE"), EMAIL("EMAIL");

  private final String modeType;

  Mode(String modeType) {
    this.modeType = modeType;
  }
}
