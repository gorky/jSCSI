
package org.jscsi.scsi.protocol.mode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;

public class ControlExtension extends ModePage
{
   public static final byte PAGE_CODE = 0x0A;
   public static final int SUBPAGE_CODE = 0x01;
   public static final int PAGE_LENGTH = 0x1C;

   private boolean TCMOS;
   private boolean SCSIP;
   private boolean IALUAE;
   private int initialPriority;

   public ControlExtension()
   {
      super(PAGE_CODE, SUBPAGE_CODE, PAGE_LENGTH);
   }

   @Override
   protected void decodeModeParameters(int dataLength, DataInputStream inputStream)
         throws BufferUnderflowException, IllegalArgumentException
   {
      try
      {
         int b5 = inputStream.readUnsignedByte();
         int b6 = inputStream.readUnsignedByte();

         // byte 5
         this.TCMOS = ((b5 >>> 3) & 1) == 1;
         this.SCSIP = ((b5 >>> 2) & 1) == 1;
         this.IALUAE = (b5 & 1) == 1;

         // byte 6
         this.initialPriority = (b6 & 0xF);
      }
      catch (IOException e)
      {
         throw new IllegalArgumentException("Error reading input data.");
      }
   }

   @Override
   protected void encodeModeParameters(DataOutputStream output)
   {
      try
      {
         // byte #5
         int b = 0;
         if (this.TCMOS)
         {
            b |= 4;
         }
         if (this.SCSIP)
         {
            b |= 2;
         }
         if (this.IALUAE)
         {
            b |= 1;
         }
         output.writeByte(b);

         // byte #6
         output.writeByte(this.initialPriority);

         // byte #7 - 32
         for (int i = 0; i < 26; i++)
         {
            output.writeByte(0);
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException("Unable to encode CDB.");
      }
   }

   public boolean isTCMOS()
   {
      return this.TCMOS;
   }

   public void setTCMOS(boolean tcmos)
   {
      this.TCMOS = tcmos;
   }

   public boolean isSCSIP()
   {
      return this.SCSIP;
   }

   public void setSCSIP(boolean scsip)
   {
      this.SCSIP = scsip;
   }

   public boolean isIALUAE()
   {
      return this.IALUAE;
   }

   public void setIALUAE(boolean ialuae)
   {
      this.IALUAE = ialuae;
   }

   public int getInitialPriority()
   {
      return this.initialPriority;
   }

   public void setInitialPriority(int initialPriority)
   {
      this.initialPriority = initialPriority;
   }
}
