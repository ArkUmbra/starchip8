package com.arkumbra.chip8.opcode.impl;

import com.arkumbra.chip8.machine.DataRegister;
import com.arkumbra.chip8.machine.Machine;
import com.arkumbra.chip8.machine.RegisterLabel;
import com.arkumbra.chip8.machine.DataRegisters;
import com.arkumbra.chip8.bitmask.BitMask;
import com.arkumbra.chip8.bitmask.BitMasks;
import com.arkumbra.chip8.opcode.OpCode;
import com.arkumbra.chip8.opcode.OpCodeLabel;

public class Op8XY3 implements OpCode {

  @Override
  public BitMask getBitMask() {
    return BitMasks.THREE_COL;
  }

  @Override
  public OpCodeLabel getOpCodeLabel() {
    return OpCodeLabel.Op8XY3BitwiseXOR;
  }

  @Override
  public void execute(char dataRegisters, Machine machine) {
    char dataRegisterXRaw = (char)(dataRegisters >> 8); // get left byte
    char dataRegisterYRaw = (char)((dataRegisters & 0x0F0) >> 4);

    DataRegisters registers = machine.getRegisters();
    DataRegister dataRegisterX = registers.getRegister(RegisterLabel.toKey(dataRegisterXRaw));
    DataRegister dataRegisterY = registers.getRegister(RegisterLabel.toKey(dataRegisterYRaw));

    char xorResult = (char) (dataRegisterX.get() ^ dataRegisterY.get());
    dataRegisterX.set(xorResult);
  }

}
