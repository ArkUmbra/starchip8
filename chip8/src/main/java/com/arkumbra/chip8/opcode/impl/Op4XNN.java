package com.arkumbra.chip8.opcode.impl;

import com.arkumbra.chip8.machine.DataRegister;
import com.arkumbra.chip8.machine.Machine;
import com.arkumbra.chip8.machine.RegisterLabel;
import com.arkumbra.chip8.bitmask.BitMask;
import com.arkumbra.chip8.bitmask.BitMasks;
import com.arkumbra.chip8.opcode.OpCode;
import com.arkumbra.chip8.opcode.OpCodeLabel;

public class Op4XNN implements OpCode {

  @Override
  public BitMask getBitMask() {
    return BitMasks.THREE_COL;
  }

  @Override
  public OpCodeLabel getOpCodeLabel() {
    return OpCodeLabel.Op4XNNSkip;
  }

  @Override
  public void execute(char dataRegisterAndData, Machine machine) {
    char dataRegisterRaw = (char)(dataRegisterAndData >> 8); // get left byte
    char data = BitMasks.TWO_COL.applyMask(dataRegisterAndData); // get right byte

    RegisterLabel registerLabel = RegisterLabel.toKey(dataRegisterRaw);
    DataRegister dataRegister = machine.getRegisters().getRegister(registerLabel);

    char valueInRegister = dataRegister.get();

    if (data != valueInRegister) {
      machine.getProgramCounter().skipNextInstruction();
    }
  }

}
