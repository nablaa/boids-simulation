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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * This class provides saving and loading using XStream XML serialize library.
 */
public class XMLSaveFile implements SaveFile {

    @Override
    public Simulation loadSimulation(File file) throws IOException {
        Simulation sim = null;
        try {
            FileReader fr = new FileReader(file);
            XStream xs = new XStream(new DomDriver());
            sim = (Simulation) xs.fromXML(fr);
            fr.close();
        } catch (Exception e) {
            throw new IOException();
        }
        return sim;
    }

    @Override
    public void saveSimulation(Simulation sim, File file) throws IOException {
        FileWriter fw = new FileWriter(file);
        XStream xs = new XStream();
        xs.toXML(sim, fw);
        fw.close();
    }

    @Override
    public String getFilenameDescription() {
        return "XML save file (*.xml)";
    }

    @Override
    public String getFilenameExtension() {
        return "xml";
    }

}
