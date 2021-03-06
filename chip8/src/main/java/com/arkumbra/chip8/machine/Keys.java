package com.arkumbra.chip8.machine;

public interface Keys extends KeyPressListener {

  Key getKey(KeyLabel keyLabel);

  /**
   * Blocks until next key is pressed
   * @return label of whichever key was pressed
   */
  KeyLabel waitForNextKeyPress() throws InterruptedException;

}
