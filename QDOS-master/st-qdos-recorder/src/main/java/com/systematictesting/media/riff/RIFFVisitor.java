package com.systematictesting.media.riff;

import com.systematictesting.media.AbortException;
import com.systematictesting.media.ParseException;

public interface RIFFVisitor {

	public boolean enteringGroup(RIFFChunk group);

	public void enterGroup(RIFFChunk group) throws ParseException, AbortException;

	public void leaveGroup(RIFFChunk group) throws ParseException, AbortException;

	public void visitChunk(RIFFChunk group, RIFFChunk chunk) throws ParseException, AbortException;
}
