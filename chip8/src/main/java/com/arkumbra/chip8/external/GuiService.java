package com.arkumbra.chip8.external;


import com.arkumbra.chip8.machine.KeyPressListener;
import com.arkumbra.chip8.machine.ScreenMemory;

public interface GuiService {

  void init(ScreenMemory screenMemory, KeyPressListener keyPressListener);

}
