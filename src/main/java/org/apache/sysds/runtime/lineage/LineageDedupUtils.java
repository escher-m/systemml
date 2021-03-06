/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sysds.runtime.lineage;

import org.apache.sysds.runtime.DMLRuntimeException;
import org.apache.sysds.runtime.controlprogram.BasicProgramBlock;
import org.apache.sysds.runtime.controlprogram.ForProgramBlock;
import org.apache.sysds.runtime.controlprogram.IfProgramBlock;
import org.apache.sysds.runtime.controlprogram.ProgramBlock;
import org.apache.sysds.runtime.controlprogram.context.ExecutionContext;

public class LineageDedupUtils {
	
	public static LineageDedupBlock computeDedupBlock(ForProgramBlock fpb, ExecutionContext ec) {
		LineageDedupBlock ldb = new LineageDedupBlock();
		ec.getLineage().pushInitDedupBlock(ldb);
		ldb.addBlock();
		for (ProgramBlock pb : fpb.getChildBlocks()) {
			if (pb instanceof IfProgramBlock)
				ldb.traceIfProgramBlock((IfProgramBlock) pb, ec);
			else if (pb instanceof BasicProgramBlock)
				ldb.traceBasicProgramBlock((BasicProgramBlock) pb, ec);
			else if (pb instanceof ForProgramBlock)
				ldb.splitBlocks();
			else
				throw new DMLRuntimeException("Only BasicProgramBlocks or "
					+ "IfProgramBlocks are allowed inside a LineageDedupBlock.");
		}
		ldb.removeLastBlockIfEmpty();
		ec.getLineage().popInitDedupBlock();
		return ldb;
	}
}
