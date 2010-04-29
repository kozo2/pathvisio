// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.pathvisio.model;

import java.io.File;

/** implemented by classes that can import a pathway from various different types */
public interface PathwayImporter {
	public String getName();

	/**
	 * Get the possible extensions this importer can read (e.g. txt).
	 * The extensions must be unique, the correct importer will be chosen
	 * based on file extension.
	 * @return An array with the possible extensions (without '.')
	 */
	public String[] getExtensions();

	/**
	 * @param File that contains pathway information
	 * @returns the result of the import, a fresh Pathway instance
	 * @throws ConverterException if the input file could not be read or parsed,
	 * 		or doesn't contain correct pathway information.
	 */
	public Pathway doImport(File file) throws ConverterException;
}
