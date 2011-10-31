//Cleversafe open-source code header - Version 1.1 - December 1, 2006
//
//Cleversafe Dispersed Storage(TM) is software for secure, private and
//reliable storage of the world's data using information dispersal.
//
//Copyright (C) 2005-2007 Cleversafe, Inc.
//
//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either version 2
//of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
//USA.
//
//Contact Information: 
// Cleversafe, 10 W. 35th Street, 16th Floor #84,
// Chicago IL 60616
// email: licensing@cleversafe.org
//
//END-OF-HEADER
//-----------------------
//@author: John Quigley <jquigley@cleversafe.com>
//@date: January 1, 2008
//---------------------

package org.jscsi.scsi.protocol.cdb;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.jscsi.scsi.protocol.util.ByteBufferInputStream;

public class ReportSupportedTaskManagementFunctions extends AbstractParameterCDB
{
   public static final int OPERATION_CODE = 0xA3;
   public static final int SERVICE_ACTION = 0x0D;

   public ReportSupportedTaskManagementFunctions()
   {
      super(OPERATION_CODE);
   }

   public ReportSupportedTaskManagementFunctions(
         boolean linked,
         boolean normalACA,
         long allocationLength)
   {
      super(OPERATION_CODE, linked, normalACA, allocationLength, 0);
      if (allocationLength > 65536)
      {
         throw new IllegalArgumentException("Allocation length out of bounds for command type");
      }
   }

   public ReportSupportedTaskManagementFunctions(long allocationLength)
   {
      this(false, false, allocationLength);
   }

   public void decode(byte[] header, ByteBuffer input) throws IOException
   {
      DataInputStream in = new DataInputStream(new ByteBufferInputStream(input));

      int operationCode = in.readUnsignedByte();
      int serviceAction = in.readUnsignedByte() & 0x1F;
      in.readInt();
      long mss = in.readUnsignedShort();
      long lss = in.readUnsignedShort();
      setAllocationLength((mss << 16) | lss);
      in.readByte();
      super.setControl(in.readUnsignedByte());

      if (operationCode != OPERATION_CODE)
      {
         throw new IllegalArgumentException("Invalid operation code: "
               + Integer.toHexString(operationCode));
      }
      if (serviceAction != SERVICE_ACTION)
      {
         throw new IOException("Invalid service action: " + Integer.toHexString(serviceAction));
      }
   }

   public byte[] encode()
   {
      ByteArrayOutputStream cdb = new ByteArrayOutputStream(this.size());
      DataOutputStream out = new DataOutputStream(cdb);

      try
      {
         out.writeByte(OPERATION_CODE);
         out.writeByte(SERVICE_ACTION);
         out.writeInt(0);
         out.writeInt((int) getAllocationLength());
         out.writeByte(0);
         out.writeByte(super.getControl());

         return cdb.toByteArray();
      }
      catch (IOException e)
      {
         throw new RuntimeException("Unable to encode CDB.");
      }
   }

   public int size()
   {
      return 12;
   }

   public int getServiceAction()
   {
      return SERVICE_ACTION;
   }
}