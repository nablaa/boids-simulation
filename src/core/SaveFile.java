/*
 *  Copyright (C) 2008 Miika-Petteri Matikainen
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package core;

import java.io.File;
import java.io.IOException;

/**
 * An interface to provide saving and loading facilities to the simulation.
 */
public interface SaveFile {
    
    /**
     * Saves the simulation to a file.
     * @param sim simulation to save
     * @param file simulation is saved to this file
     * @throws IOException throws an exception is thrown if the save is unsuccessful
     */
    public void saveSimulation(Simulation sim, File file) throws IOException;

    /**
     * Loads a simulation from the given file reader.
     * @param file simulation is loaded from this file
     * @return loaded simulation
     * @throws IOException throws and exception is thrown if the load is unsuccessful
     */
    public Simulation loadSimulation(File file) throws IOException;
    
    /**
     * Gets the file format description. Used in the file dialog.
     * @return file format description
     */
    public String getFilenameDescription();
    
    /**
     * Gets the file format extension. Used in the file dialog.
     * @return file format extension
     */
    public String getFilenameExtension();
    
}
