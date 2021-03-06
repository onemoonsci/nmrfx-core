/*
 * NMRFx Structure : A Program for Calculating Structures
 * Copyright (C) 2004-2017 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.nmrfx.chemistry.io;

import org.nmrfx.chemistry.relax.RelaxationRex;
import org.nmrfx.chemistry.relax.RelaxationData;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.nmrfx.chemistry.*;
import org.nmrfx.peaks.PeakList;
import org.nmrfx.peaks.InvalidPeakException;
import org.nmrfx.peaks.io.PeakPathWriter;
import org.nmrfx.peaks.io.PeakWriter;
import org.nmrfx.star.ParseException;
import org.nmrfx.star.STAR3;
import org.nmrfx.peaks.PeakPaths;
import org.nmrfx.chemistry.constraints.ConstraintSet;
import org.nmrfx.peaks.ResonanceFactory;
import org.nmrfx.chemistry.relax.RelaxationData.relaxTypes;

/**
 *
 * @author brucejohnson
 */
public class NMRStarWriter {

    protected static final String[] entityCompIndexLoopStrings = {"_Entity_comp_index.ID", "_Entity_comp_index.Auth_seq_ID", "_Entity_comp_index.Comp_ID", "_Entity_comp_index.Comp_label", "_Entity_comp_index.Entity_ID"};
    protected static final String[] entityAssemblyLoopStrings = {"_Entity_assembly.ID", "_Entity_assembly.Entity_assembly_name", "_Entity_assembly.Entity_ID", "_Entity_assembly.Entity_label", "_Entity_assembly.Asym_ID", "_Entity_assembly.Experimental_data_reported", "_Entity_assembly.Physical_state", "_Entity_assembly.Conformational_isomer", "_Entity_assembly.Chemical_exchange_state", "_Entity_assembly.Magnetic_equivalence_group_code", "_Entity_assembly.Role", "_Entity_assembly.Details", "_Entity_assembly.Assembly_ID"};
    private static final String[] entityCommonNameLoopStrings = {"_Entity_common_name.Name", "_Entity_common_name.Type", "_Entity_common_name.Entity_ID"};
    protected static final String[] entityBondLoopStrings = {"_Entity_bond.ID", "_Entity_bond.Type", "_Entity_bond.Value_order", "_Entity_bond.Comp_index_ID_1", "_Entity_bond.Comp_ID_1", "_Entity_bond.Atom_ID_1", "_Entity_bond.Comp_index_ID_2", "_Entity_bond.Comp_ID_2", "_Entity_bond.Atom_ID_2", "_Entity_bond.Entity_ID"};
    private static final String[] chemCompEntityIndexLoopStrings = {"_Entity_comp_index.ID", "_Entity_comp_index.Auth_seq_ID", "_Entity_comp_index.Comp_ID", "_Entity_comp_index.Comp_label", "_Entity_comp_index.Entry_ID", "_Entity_comp_index.Entity_ID"};
    protected static final String[] entityChemCompDeletedLoopStrings = {"_Entity_chem_comp_deleted_atom.ID", "_Entity_chem_comp_deleted_atom.Comp_index_ID", "_Entity_chem_comp_deleted_atom.Comp_ID", "_Entity_chem_comp_deleted_atom.Atom_ID", "_Entity_chem_comp_deleted_atom.Entity_ID"};
    private static final String[] chemCompBondLoopStrings = {"_Chem_comp_bond.Bond_ID", "_Chem_comp_bond.Type", "_Chem_comp_bond.Value_order", "_Chem_comp_bond.Atom_ID_1", "_Chem_comp_bond.Atom_ID_2", "_Chem_comp_bond.PDB_atom_ID_1", "_Chem_comp_bond.PDB_atom_ID_2", "_Chem_comp_bond.Details", "_Chem_comp_bond.Entry_ID", "_Chem_comp_bond.Comp_ID"};
    public static String[] entityPolySeqLoopStrings = {"_Entity_poly_seq.Hetero", "_Entity_poly_seq.Mon_ID", "_Entity_poly_seq.Num", "_Entity_poly_seq.Comp_index_ID", "_Entity_poly_seq.Entity_ID"};
    private static final String[] chemCompAtomLoopStrings = {"_Chem_comp_atom.Atom_ID", "_Chem_comp_atom.PDB_atom_ID", "_Chem_comp_atom.Alt_atom_ID", "_Chem_comp_atom.Auth_atom_ID", "_Chem_comp_atom.Type_symbol", "_Chem_comp_atom.Isotope_number", "_Chem_comp_atom.Chirality", "_Chem_comp_atom.Charge", "_Chem_comp_atom.Partial_charge", "_Chem_comp_atom.Oxidation_number", "_Chem_comp_atom.PDBx_aromatic_flag", "_Chem_comp_atom.PDBx_leaving_atom_flag", "_Chem_comp_atom.Substruct_code", "_Chem_comp_atom.Ionizable", "_Chem_comp_atom.Details", "_Chem_comp_atom.Entry_ID", "_Chem_comp_atom.Comp_ID", "_Chem_comp_atom.Unpaired_electron_number"};
    static String[] chemShiftAssignmentStrings = {"_Atom_chem_shift.ID", "_Atom_chem_shift.Assembly_atom_ID", "_Atom_chem_shift.Entity_assembly_ID", "_Atom_chem_shift.Entity_ID", "_Atom_chem_shift.Comp_index_ID", "_Atom_chem_shift.Seq_ID", "_Atom_chem_shift.Comp_ID", "_Atom_chem_shift.Atom_ID", "_Atom_chem_shift.Atom_type", "_Atom_chem_shift.Atom_isotope_number", "_Atom_chem_shift.Val", "_Atom_chem_shift.Val_err", "_Atom_chem_shift.Assign_fig_of_merit", "_Atom_chem_shift.Ambiguity_code", "_Atom_chem_shift.Occupancy", "_Atom_chem_shift.Resonance_ID", "_Atom_chem_shift.Auth_seq_ID", "_Atom_chem_shift.Auth_comp_ID", "_Atom_chem_shift.Auth_atom_ID", "_Atom_chem_shift.Details", "_Atom_chem_shift.Assigned_chem_shift_list_ID"};
    private static String[] atomCoordinateLoopStrings = {"_Atom_site.Assembly_ID", "_Atom_site.Model_ID", "_Atom_site.Model_site_ID", "_Atom_site.ID", "_Atom_site.Assembly_atom_ID", "_Atom_site.Label_entity_assembly_ID", "_Atom_site.Label_entity_ID", "_Atom_site.Label_comp_index_ID", "_Atom_site.Label_comp_ID", "_Atom_site.Label_atom_ID", "_Atom_site.Type_symbol", "_Atom_site.Cartn_x", "_Atom_site.Cartn_y", "_Atom_site.Cartn_z", "_Atom_site.Cartn_x_esd", "_Atom_site.Cartn_y_esd", "_Atom_site.Cartn_z_esd", "_Atom_site.Occupancy", "_Atom_site.Occupancy_esd", "_Atom_site.Uncertainty", "_Atom_site.Ordered_flag", "_Atom_site.Footnote_ID", "_Atom_site.Details", "_Atom_site.Entry_ID", "_Atom_site.Conformer_family_coord_set_ID"};

    static String toSTAR3CompoundString(int ID, Atom atom, int entityID) {
        StringBuilder result = new StringBuilder();
        String sep = " ";
        result.append(atom.getName());
        result.append(sep);
        result.append(atom.getName());
        result.append(sep);
        result.append("?");
        result.append(sep);
        result.append("?");
        result.append(sep);
        result.append(Atom.getElementName(atom.getAtomicNumber()));
        result.append(sep);
        result.append("?");
        result.append(sep);
        result.append("N");
        result.append(sep);
        result.append(0);
        result.append(sep);
        result.append("?");
        result.append(sep);
        result.append("?");
        result.append(sep);
        result.append("N");
        result.append(sep);
        result.append("N");
        result.append(sep);
        result.append("?");
        result.append(sep);
        result.append("?");
        result.append(sep);
        result.append("?");
        result.append(sep);
        result.append(entityID);
        result.append(sep);
        result.append(((Compound) atom.entity).label);
        result.append(sep);
        result.append("?");
        return result.toString();
    }

    public static void writeEntityCommonNamesSTAR3(FileWriter chan, Entity entity, int entityID) throws IOException {
        if (entity.getCommonNames().size() > 0) {
            String[] loopStrings = NMRStarWriter.entityCommonNameLoopStrings;
            chan.write("loop_\n");
            for (String loopString : loopStrings) {
                chan.write(loopString + "\n");
            }
            chan.write("\n");
            for (Entity.EntityCommonName eCN : entity.getCommonNames()) {
                chan.write(toSTAR3CommonNameString(eCN, entityID));
                chan.write("\n");
            }
            chan.write("stop_\n");
            chan.write("\n");
        }
    }

    static String toSTAR3String(Entity entity, String coordSetName, int assemblyID, int compID) {
        StringBuilder result = new StringBuilder();
        String sep = " ";
        result.append(compID);
        result.append(sep);
        result.append("\"").append(entity.name).append("\"");
        result.append(sep);
        result.append(entity.entityID);
        result.append(sep);
        result.append("$").append(entity.label);
        result.append(sep);
        result.append(coordSetName);
        result.append(sep);
        result.append("yes");
        result.append(sep);
        result.append(entity.physicalState);
        result.append(sep);
        result.append(entity.conformationalIsomer);
        result.append(sep);
        result.append(entity.chemicalExchangeState);
        result.append(sep);
        result.append(entity.magneticEquivalenceGroupCode);
        result.append(sep);
        result.append(entity.role);
        result.append(sep);
        result.append(entity.details);
        result.append(sep);
        result.append(assemblyID);
        result.append(sep);
        return result.toString();
    }

    static String toSTAR3String(Polymer polymer, String coordSetName, int assemblyID, int compID) {
        StringBuilder result = new StringBuilder();
        String sep = " ";
        result.append(compID);
        result.append(sep);
        result.append("\"").append(polymer.name).append("\"");
        result.append(sep);
        result.append(polymer.entityID);
        result.append(sep);
        result.append("$").append(polymer.label);
        result.append(sep);
        result.append(coordSetName);
        result.append(sep);
        result.append("yes");
        result.append(sep);
        result.append(polymer.physicalState);
        result.append(sep);
        result.append(polymer.conformationalIsomer);
        result.append(sep);
        result.append(polymer.chemicalExchangeState);
        result.append(sep);
        result.append(polymer.magneticEquivalenceGroupCode);
        result.append(sep);
        result.append(polymer.role);
        result.append(sep);
        result.append(polymer.details);
        result.append(sep);
        result.append(assemblyID);
        result.append(sep);
        return result.toString();
    }

    static String toSTAR3AtomIndexString(final AtomSpecifier atom) {
        StringBuilder result = new StringBuilder();
        String sep = " ";
        result.append(atom.getResNum());
        result.append(sep);
        result.append(atom.getResName());
        result.append(sep);
        result.append(atom.getName());
        return result.toString();
    }

    static void writeEntityHeaderSTAR3(FileWriter chan, Compound compound, int entityID) throws ParseException, IOException {
        String label = compound.label;
        chan.write("save_" + label + "\n");
        chan.write("_Entity.Sf_category                 ");
        chan.write("entity\n");
        chan.write("_Entity.framecode                           ");
        chan.write(label + "\n");
        chan.write("_Entity.ID                           ");
        chan.write(entityID + "\n");
        chan.write("_Entity.Name                        ");
        chan.write(label + "\n");
        chan.write("_Entity.Type                          ");
        chan.write("non-polymer\n");
        chan.write("\n");
        STAR3.writeLoopStrings(chan, chemCompEntityIndexLoopStrings);
        StringBuilder result = new StringBuilder();
        String sep = " ";
        result.append("1");
        result.append(sep);
        result.append(compound.getNumber());
        result.append(sep);
        result.append(label);
        result.append(sep);
        result.append(label);
        result.append(sep);
        result.append(".");
        result.append(sep);
        result.append(entityID);
        chan.write(result.toString());
        chan.write("\nstop_\n\n");
    }

    public static void writeComponentsSTAR3(FileWriter chan, Polymer polymer, Set<String> cmpdSet) throws IOException, ParseException {
        Iterator residueIterator = polymer.iterator();
        int i = 1;
        while (residueIterator.hasNext()) {
            Residue residue = (Residue) residueIterator.next();
            String mode;
            if (i == 1) {
                mode = "." + i;
            } else if (!residueIterator.hasNext()) {
                mode = "." + i;
            } else {
                mode = "";
            }
            if (!cmpdSet.contains(residue.label + mode)) {
                writeCompoundToSTAR3(chan, residue, i, mode);
                cmpdSet.add(residue.label + mode);
            }
            i++;
        }
    }

    static void writeEntityHeaderSTAR3(FileWriter chan, Entity entity, int entityID) throws IOException {
        String label = entity.label;
        chan.write("save_" + label + "\n");
        chan.write("_Entity.Sf_category                 ");
        chan.write("entity\n");
        chan.write("_Entity.framecode                           ");
        chan.write(label + "\n");
        chan.write("_Entity.ID                           ");
        chan.write(entityID + "\n");
        chan.write("_Entity.Name                        ");
        chan.write(label + "\n");
        chan.write("_Entity.Type                          ");
        if (entity instanceof Polymer) {
            Polymer polymer = (Polymer) entity;
            chan.write("polymer\n");
            chan.write("_Entity.Polymer_type                  ");
            chan.write(polymer.getPolymerType() + "\n");
            chan.write("_Entity.Polymer_strand_ID            ");
            String strandID = polymer.getStrandID();
            if (strandID.equals("")) {
                strandID = "?";
            }
            chan.write(strandID + "\n");
            chan.write("_Entity.Polymer_seq_one_letter_code_can  ");
            chan.write("?\n");
            chan.write("_Entity.Polymer_seq_one_letter_code       ");
            String oneLetterCode = polymer.getOneLetterCode();
            chan.write("\n;\n");
            int codeLen = oneLetterCode.length();
            boolean nonStandard = false;
            int j = 0;
            while (j < codeLen) {
                int endIndex = j + 40;
                if (endIndex > codeLen) {
                    endIndex = codeLen;
                }
                String segment = oneLetterCode.substring(j, endIndex);
                chan.write(segment);
                chan.write("\n");
                j += 40;
            }
            chan.write(";\n");
            chan.write("_Entity.Number_of_monomers            ");
            chan.write(codeLen + "\n");
            chan.write("_Entity.Nstd_monomer                  ");
            if (nonStandard) {
                chan.write("yes\n");
            } else {
                chan.write("no\n");
            }
            chan.write("\n");
            chan.write("_Entity.Nomenclature                  ");
            chan.write(polymer.getNomenclature());
            chan.write("\n");
            chan.write("_Entity.Capped                  ");
            if (polymer.isCapped()) {
                chan.write("yes\n");
            } else {
                chan.write("no\n");
            }
        }
    }

    static void writeCompoundToSTAR3(FileWriter chan, Compound compound, int entityID, final String mode) throws IOException, ParseException {
        String label = compound.label;
        chan.write("save_chem_comp_" + label + mode + "\n");
        chan.write("_Chem_comp.Sf_category                 ");
        chan.write("chem_comp\n");
        chan.write("_Chem_comp.framecode                           ");
        chan.write("chem_comp_" + label + mode + "\n");
        STAR3.writeLoopStrings(chan, chemCompAtomLoopStrings);
        int iAtom = 0;
        for (Atom atom : compound.getAtoms()) {
            chan.write(toSTAR3CompoundString(iAtom, atom, entityID));
            chan.write("\n");
        }
        chan.write("stop_\n");
        if (compound.getBonds().size() > 0) {
            STAR3.writeLoopStrings(chan, chemCompBondLoopStrings);
            int iBond = 1;
            for (Bond bond : compound.getBonds()) {
                if (bond.begin.entity == bond.end.entity) {
                    chan.write(toSTAR3CompoundBondString(iBond, bond, entityID));
                    chan.write("\n");
                    iBond++;
                }
            }
            chan.write("stop_\n");
        }
        chan.write("save_\n\n");
    }

    static String toSTAR3CompoundBondString(int ID, Bond bond, int entityID) {
        StringBuilder result = new StringBuilder();
        String sep = " ";
        result.append(ID);
        result.append(sep);
        result.append("?");
        result.append(sep);
        switch (bond.order) {
            case SINGLE:
                result.append("SING");
                break;
            case DOUBLE:
                result.append("DOUB");
                break;
            case TRIPLE:
                result.append("TRIP");
                break;
            default:
                result.append(" ?  ");
        }
        result.append(sep);
        result.append(bond.begin.getName());
        result.append(sep);
        result.append(bond.end.getName());
        result.append(sep);
        result.append(bond.begin.getName());
        result.append(sep);
        result.append(bond.end.getName());
        result.append(sep);
        result.append("?");
        result.append(sep);
        result.append(entityID);
        result.append(sep);
        result.append(((Compound) bond.begin.entity).label);
        return result.toString();
    }

    static String toSTAR3CommonNameString(Entity.EntityCommonName eCN, int entityID) {
        StringBuilder result = new StringBuilder();
        String sep = " ";
        result.append(STAR3.quote(eCN.getName()));
        result.append(sep);
        result.append(STAR3.quote(eCN.getType()));
        result.append(sep);
        result.append(entityID);
        return result.toString();
    }

    static String toSTAR3CompIndexString(int ID, Residue residue, int entityID) {
        StringBuilder result = new StringBuilder();
        String sep = " ";
        result.append(ID);
        result.append(sep);
        result.append(residue.number);
        result.append(sep);
        result.append(residue.name);
        result.append(sep);
        result.append(".");
        result.append(sep);
        result.append(entityID);
        return result.toString();
    }

    public static void writeEntitySeqSTAR3(FileWriter chan, Polymer polymer, int entityID) throws IOException {
        StringBuilder result = new StringBuilder();
        String sep = " ";
        String[] loopStrings = Polymer.entityCompIndexLoopStrings;
        chan.write("loop_\n");
        for (String loopString : loopStrings) {
            chan.write(loopString + "\n");
        }
        chan.write("\n");
        Iterator residueIterator = polymer.iterator();
        int i = 1;
        while (residueIterator.hasNext()) {
            Residue residue = (Residue) residueIterator.next();
            chan.write(toSTAR3CompIndexString(i++, residue, entityID));
            chan.write("\n");
        }
        chan.write("stop_\n");
        chan.write("\n");
        loopStrings = entityChemCompDeletedLoopStrings;
        chan.write("loop_\n");
        for (String loopString : loopStrings) {
            chan.write(loopString + "\n");
        }
        i = 1;
        for (AtomSpecifier atom : polymer.getDeletedAtoms()) {
            result.setLength(0);
            result.append(i++);
            result.append(sep);
            result.append(toSTAR3AtomIndexString(atom));
            result.append(sep);
            result.append(entityID);
            chan.write(result.toString());
            chan.write("\n");
        }
        chan.write("stop_\n");
        chan.write("\n");
        String[] orders = {"single", "double", "triple"};
        loopStrings = entityBondLoopStrings;
        chan.write("loop_\n");
        for (String loopString : loopStrings) {
            chan.write(loopString + "\n");
        }
        //    1  peptide   single   1  ALA   N  10  GLU  C  ?  1
        i = 1;
        for (BondSpecifier bond : polymer.getAddedBonds()) {
            AtomSpecifier atom1 = bond.getAtom1();
            AtomSpecifier atom2 = bond.getAtom2();
            result.setLength(0);
            result.append(i++);
            result.append(sep);
            result.append('?');
            result.append(sep);
            result.append(orders[bond.getOrder().getOrderNum() - 1]);
            result.append(sep);
            result.append(toSTAR3AtomIndexString(atom1));
            result.append(sep);
            result.append(toSTAR3AtomIndexString(atom2));
            result.append(sep);
            result.append(entityID);
            chan.write(result.toString());
            chan.write("\n");
        }
        chan.write("stop_\n");
        chan.write("\n");
    }

    public static void writeMoleculeSTAR3(FileWriter chan, MoleculeBase molecule, int assemblyID) throws IOException, ParseException {
        Iterator entityIterator = molecule.entityLabels.values().iterator();
        Set<String> cmpdSet = new HashSet<>();
        int entityID = 1;
        chan.write("\n\n");
        chan.write("    ####################################\n");
        chan.write("    #  Biological polymers and ligands #\n");
        chan.write("    ####################################\n");
        chan.write("\n\n");
        while (entityIterator.hasNext()) {
            Entity entity = (Entity) entityIterator.next();
            if (entity instanceof Polymer) {
                writeEntityHeaderSTAR3(chan, entity, entityID);
                writeEntityCommonNamesSTAR3(chan, entity, entityID);
                Polymer polymer = (Polymer) entity;
                writeEntitySeqSTAR3(chan, polymer, entityID);
                chan.write("save_\n\n");
                if (!polymer.getNomenclature().equals("IUPAC") && !polymer.getNomenclature().equals("XPLOR")) {
                    writeComponentsSTAR3(chan, polymer, cmpdSet);
                }
            } else {
                writeEntityHeaderSTAR3(chan, (Compound) entity, entityID);
                chan.write("save_\n\n");
                writeCompoundToSTAR3(chan, (Compound) entity, entityID, "");
            }
            entityID++;
        }
        String name = molecule.getName();
        chan.write("\n\n");
        chan.write("    #############################################\n");
        chan.write("    #  Molecular system (assembly) description  #\n");
        chan.write("    #############################################\n");
        chan.write("\n\n");
        chan.write("save_" + "assembly\n");
        chan.write("_Assembly.Sf_category                 ");
        chan.write("assembly\n");
        chan.write("_Assembly.Sf_framecode                 ");
        chan.write("assembly\n");
        chan.write("_Assembly.Entry_ID                    ");
        chan.write(".\n");
        chan.write("_Assembly.ID                          ");
        chan.write(assemblyID + "\n");
        chan.write("_Assembly.Name               ");
        if (name == null) {
            chan.write("null\n");
        } else {
            chan.write(STAR3.quote(name) + "\n");
        }
        chan.write("_Assembly.Number_of_components                   ");
        int nEntities = molecule.entities.size();
        chan.write(nEntities + "\n");
        for (String key : molecule.getPropertyNames()) {
            String propValue = molecule.getProperty(key);
            if ((propValue != null) && (!propValue.equals(""))) {
                chan.write("_Assembly.NvJ_prop_" + key + "                   ");
                STAR3.writeString(chan, propValue, 1024);
            }
        }
        chan.write("\n");
        STAR3.writeLoopStrings(chan, entityAssemblyLoopStrings);
        int compID = 1;
        for (CoordSet coordSet : molecule.coordSets.values()) {
            for (Entity entity : coordSet.entities.values()) {
                chan.write(toSTAR3String(entity, coordSet.getName(), assemblyID, compID) + "\n");
                compID++;
            }
        }
        chan.write("stop_\n");
        chan.write("\n");
        chan.write("save_\n");
    }

    public static String toSTARChemShiftAssignmentString(final SpatialSet spatialSet, final int id, final int ppmSet) {
        StringBuilder result = new StringBuilder();
        String sep = " ";
        Atom atom = spatialSet.atom;
        result.append(id);
        result.append(sep);
        result.append(".");
        result.append(sep);
        Entity entity = atom.getEntity();
        int entityID = entity.getIDNum();
        int entityAssemblyID = entity.assemblyID;
        int number = 1;
        String seqNumber = "1";
        if (entity instanceof Residue) {
            entityID = ((Residue) entity).polymer.getIDNum();
            entityAssemblyID = ((Residue) entity).polymer.assemblyID;
            number = atom.getEntity().getIDNum();
        }
        result.append(entityAssemblyID);
        result.append(sep);
        result.append(entityID);
        result.append(sep);
        result.append(number);
        result.append(sep);
        result.append(number);
        result.append(sep);
        result.append(atom.getEntity().getName());
        result.append(sep);
        result.append(atom.getName());
        result.append(sep);
        String eName = AtomProperty.getElementName(atom.getAtomicNumber());
        result.append(eName);
        result.append(sep);
        result.append(".");
        result.append(sep);
        PPMv ppmv = (PPMv) spatialSet.getPPM(ppmSet);
        if ((ppmv != null) && ppmv.isValid()) {
            result.append(String.format("%.4f", ppmv.getValue()));
            result.append(sep);
            result.append(String.format("%.4f", ppmv.getError()));
        } else {
            result.append(".");
            result.append(sep);
            result.append(".");
        }
        result.append(sep);
        result.append(".");
        result.append(sep);
        if ((ppmv != null) && ppmv.isValid()) {
            int ambig = ppmv.getAmbigCode();
            if (ambig < 1) {
                ambig = atom.getBMRBAmbiguity();
            }
            result.append(ambig);
        } else {
            result.append(".");
        }
        result.append(sep);
        result.append(".");
        result.append(sep);
        String resIDStr = ".";
        if (atom.getResonance() != null) {
            long resID = atom.getResonance().getID();
            resIDStr = String.valueOf(resID);
        }
        result.append(resIDStr);
        result.append(sep);
        String rNum = ((Compound) atom.getEntity()).getNumber();
        if (rNum.trim().length() == 0) {
            rNum = ".";
        }
        result.append(rNum);
        result.append(sep);
        result.append(atom.getEntity().getName());
        result.append(sep);
        result.append(atom.getName());
        result.append(sep);
        result.append(".");
        result.append(sep);
        result.append(1);
        return result.toString();
    }

    static void writeAssignmentsSTAR3(FileWriter chan, final int ppmSet) throws IOException, ParseException, InvalidMoleculeException {
        chan.write("\n\n");
        chan.write("    ###################################\n");
        chan.write("    #  Assigned chemical shift lists  #\n");
        chan.write("    ###################################\n");
        chan.write("\n\n");
        chan.write("###################################################################\n");
        chan.write("#       Chemical Shift Ambiguity Index Value Definitions          #\n");
        chan.write("#                                                                 #\n");
        chan.write("#   Index Value            Definition                             #\n");
        chan.write("#                                                                 #\n");
        chan.write("#      1             Unique (geminal atoms and geminal methyl     #\n");
        chan.write("#                         groups with identical chemical shifts   #\n");
        chan.write("#                         are assumed to be assigned to           #\n");
        chan.write("#                         stereospecific atoms)                   #\n");
        chan.write("#      2             Ambiguity of geminal atoms or geminal methyl #\n");
        chan.write("#                         proton groups                           #\n");
        chan.write("#      3             Aromatic atoms on opposite sides of          #\n");
        chan.write("#                         symmetrical rings (e.g. Tyr HE1 and HE2 #\n");
        chan.write("#                         protons)                                #\n");
        chan.write("#      4             Intraresidue ambiguities (e.g. Lys HG and    #\n");
        chan.write("#                         HD protons or Trp HZ2 and HZ3 protons)  #\n");
        chan.write("#      5             Interresidue ambiguities (Lys 12 vs. Lys 27) #\n");
        chan.write("#      9             Ambiguous, specific ambiguity not defined    #\n");
        chan.write("#                                                                 #\n");
        chan.write("###################################################################\n");
        chan.write("\n\n");
        chan.write("save_assigned_chem_shift_list_" + ppmSet + "\n");
        chan.write("_Assigned_chem_shift_list.Sf_category                 ");
        chan.write("assigned_chemical_shifts\n");
        chan.write("_Assigned_chem_shift_list.Sf_framecode                 ");
        chan.write("assigned_chem_shift_list_1\n");
        chan.write("_Assigned_chem_shift_list.Sample_condition_list_ID      ");
        chan.write(".\n");
        chan.write("_Assigned_chem_shift_list.Sample_condition_list_label    ");
        chan.write(".\n");
        chan.write("_Assigned_chem_shift_list.Chem_shift_reference_ID        ");
        chan.write(".\n");
        chan.write("_Assigned_chem_shift_list.Chem_shift_reference_label      ");
        chan.write(".\n");
        chan.write("\n");
        STAR3.writeLoopStrings(chan, chemShiftAssignmentStrings);
        boolean wroteAtLeastOne = false;
        int iAtom = 1;
        List<Atom> atoms = new ArrayList();
        MolFilter molFilter = new MolFilter("*.*");
        MoleculeBase.selectAtomsForTable(molFilter, atoms);
        for (int i = 0; i < atoms.size(); i++) {
            SpatialSet spatialSet = atoms.get(i).getSpatialSet();
            PPMv ppmv = (PPMv) spatialSet.getPPM(ppmSet);
            if ((ppmv != null) && ppmv.isValid()) {
                String string = NMRStarWriter.toSTARChemShiftAssignmentString(spatialSet, iAtom, ppmSet);
                if (string != null) {
                    chan.write(string + "\n");
                    iAtom++;
                    wroteAtLeastOne = true;
                }
            }
        }
        if (!wroteAtLeastOne) {
            chan.write("? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ?\n");
        }
        chan.write("stop_\n");
        chan.write("\nsave_\n");
        chan.write("\n");
    }

    public static void writePPM(MoleculeBase molecule, FileWriter chan, int whichStruct) throws IOException, InvalidMoleculeException {
        int i;
        chan.write("loop_\n");
        chan.write("  _Atom_shift_assign_ID\n");
        chan.write("  _Residue_author_seq_code\n");
        chan.write("  _Residue_seq_code\n");
        chan.write("  _Residue_label\n");
        chan.write("  _Atom_name\n");
        chan.write("  _Atom_type\n");
        chan.write("  _Chem_shift_value\n");
        chan.write("  _Chem_shift_value_error\n");
        chan.write("  _Chem_shift_ambiguity_code\n");
        if (molecule == null) {
            throw new InvalidMoleculeException("No active mol");
        }
        int iPPM = 0;
        i = 0;
        molecule.updateAtomArray();
        for (Atom atom : molecule.getAtomArray()) {
            String result = atom.ppmToString(iPPM, i);
            if (result != null) {
                chan.write(result + "\n");
                i++;
            }
        }
        chan.write("\nstop_\n\n");
    }

    public static void writeXYZ(MoleculeBase molecule, FileWriter chan, int whichStruct) throws IOException, InvalidMoleculeException {
        int i = 0;
        int iStruct;
        chan.write("loop_\n");
        chan.write("  _Atom_ID\n");
        if (whichStruct < 0) {
            chan.write("  _Conformer_number\n");
        }
        chan.write("  _Mol_system_component_name\n");
        chan.write("  _Residue_seq_code\n");
        chan.write("  _Residue_label\n");
        chan.write("  _Atom_name\n");
        chan.write("  _Atom_type\n");
        chan.write("  _Atom_coord_x\n");
        chan.write("  _Atom_coord_y\n");
        chan.write("  _Atom_coord_z\n");
        int[] structureList = molecule.getActiveStructures();
        for (int jStruct = 0; jStruct < structureList.length; jStruct++) {
            iStruct = structureList[jStruct];
            if ((whichStruct >= 0) && (iStruct != whichStruct)) {
                continue;
            }
            molecule.updateAtomArray();
            for (Atom atom : molecule.getAtomArray()) {
                SpatialSet spatialSet = atom.getSpatialSet();
                String result = atom.xyzToString(spatialSet, iStruct, i);
                if (result != null) {
                    chan.write(result + "\n");
                    i++;
                }
            }
        }
        chan.write("\nstop_\n\n");
    }

    public static String[] getCoordLoopStrings() {
        return atomCoordinateLoopStrings.clone();
    }

    String toSTAR3PolySeqString(int ID, Residue residue, int entityID) {
        StringBuilder result = new StringBuilder();
        String sep = " ";
        result.append(".");
        result.append(sep);
        result.append(residue.name);
        result.append(sep);
        result.append(ID);
        result.append(sep);
        result.append(ID);
        result.append(sep);
        result.append(entityID);
        return result.toString();
    }

    /**
     * Write out the NOE sections of the STAR file.
     *
     * @param chan FileWriter. The FileWriter to use
     * @param molecule Molecule. The molecule to use
     * @param noeData0 RelaxationData. The NOE dataset.
     * @param listID int. The number of the NOE block in the file.
     * @throws IOException
     * @throws InvalidMoleculeException
     */
    public static void writeNOE(FileWriter chan, MoleculeBase molecule, RelaxationData noeData0, int listID) throws IOException, InvalidMoleculeException {
        List<Atom> atoms = molecule.getAtomArray();
        String frameName = noeData0.getID();
        double field = noeData0.getField();
        chan.write("    ########################################\n");
        chan.write("    #  Heteronuclear NOE relaxation values  #\n");
        chan.write("    ########################################\n");
        chan.write("\n\n");
        chan.write("save_" + frameName + "\n");
        chan.write("   _Heteronucl_NOE_list.Sf_category                    ");
        chan.write("heteronucl_NOE_relaxation\n");
        chan.write("   _Heteronucl_NOE_list.Sf_framecode                   ");
        chan.write(frameName + "\n");
        chan.write("   _Heteronucl_NOE_list.Entry_ID                       ");
        chan.write(".\n"); //fixme get dynamically
        chan.write("   _Heteronucl_NOE_list.ID                             ");
        chan.write(listID + "\n");
        chan.write("   _Heteronucl_NOE_list.Sample_condition_list_ID       ");
        chan.write(listID + "\n");
        chan.write("   _Heteronucl_NOE_list.Sample_condition_list_label    ");
        chan.write("$sample_conditions_" + listID + "\n");
        chan.write("   _Heteronucl_NOE_list.Spectrometer_frequency_1H      ");
        chan.write(String.valueOf(field) + "\n");
        chan.write("   _Heteronucl_NOE_list.Heteronuclear_NOE_val_type      ");
        chan.write(STAR3.quote("peak height") + "\n");
        chan.write("   _Heteronucl_NOE_list.ref_val      ");
        chan.write("0\n"); //fixme get dynamically
        chan.write("   _Heteronucl_NOE_list.ref_description      ");
        chan.write(".\n");
        chan.write("   _Heteronucl_NOE_list.Details                        ");
        chan.write(".\n");

        chan.write("\n");
        chan.write("   loop_\n");
        chan.write("      _Heteronucl_NOE_experiment.Experiment_ID\n");
        chan.write("      _Heteronucl_NOE_experiment.Experiment_name\n");
        chan.write("      _Heteronucl_NOE_experiment.Sample_ID\n");
        chan.write("      _Heteronucl_NOE_experiment.Sample_label\n");
        chan.write("      _Heteronucl_NOE_experiment.Sample_state\n");
        chan.write("      _Heteronucl_NOE_experiment.Entry_ID\n");
        chan.write("      _Heteronucl_NOE_experiment.Heteronucl_NOE_list_ID\n");
        chan.write("\n");

        String nmrExpType = "2D 1H-15N HSQC"; //fixme get dynamically
        String sampleLabel = "$sample_" + listID; //fixme get dynamically
        String result1 = String.format("%-2d %-7s %-7s %-9s %-2s %-2s %-2d", listID, STAR3.quote(nmrExpType), listID, sampleLabel, ".", ".", listID);
        chan.write("      " + result1 + "\n");
        chan.write("   stop_\n\n");

        String[] loopStrings = {"ID", "Assembly_atom_ID_1", "Entity_assembly_ID_1", "Entity_ID_1", "Comp_index_ID_1", "Seq_ID_1",
            "Comp_ID_1", "Atom_ID_1", "Atom_type_1", "Atom_isotope_number_1", "Assembly_atom_ID_2", "Entity_assembly_ID_2", "Entity_ID_2",
            "Comp_index_ID_2", "Seq_ID_2", "Comp_ID_2", "Atom_ID_2", "Atom_type_2", "Atom_isotope_number_2", "Val", "Val_err",
            "Resonance_ID_1", "Resonance_ID_2", "Auth_entity_assembly_ID_1", "Auth_seq_ID_1", "Auth_comp_ID_1", "Auth_atom_ID_1",
            "Auth_entity_assembly_ID_2", "Auth_seq_ID_2", "Auth_comp_ID_2", "Auth_atom_ID_2", "Entry_ID", "Heteronucl_NOE_list_ID"};

        chan.write("   loop_\n");
        for (String loopString : loopStrings) {
            chan.write("      _Heteronucl_NOE." + loopString + "\n");
        }
        chan.write("\n");

        int idx = 1;

        List<String> prevRes = new ArrayList<>();
        Iterator entityIterator = molecule.entityLabels.values().iterator();
        while (entityIterator.hasNext()) {
            Entity entity = (Entity) entityIterator.next();
            int entityID = entity.getIDNum();
            for (Atom atom : atoms) {
                List<RelaxationData> noeDataList = atom.getRelaxationData(relaxTypes.NOE, field, null).stream()
                        .filter(r -> r.getID().contains("RING_fit")).collect(Collectors.toList());
                if (noeDataList != null) {
                    for (RelaxationData noeData : noeDataList) {
                        Double value = noeData.getValue();
                        Double error = noeData.getError();
                        Atom atom2 = noeData.getExtraAtoms().get(0);
                        String outputLine = toStarNOEString(idx, listID, entityID, atom, atom2, value, error);
                        if (outputLine != null && !prevRes.contains(entityID + "." + atom.getResidueNumber())) {
                            chan.write("      " + outputLine + "\n");
                            prevRes.add(entityID + "." + atom.getResidueNumber());
                            idx++;
                        }
                    }
                }
            }
        }

        chan.write("   stop_\n");
        chan.write("save_\n\n");

    }

    /**
     * Write the data lines in the NOE Data block of the STAR file.
     *
     * @param idx int. The line index
     * @param listID int. The number of the T1/T2/T1rho/NOE block in the file.
     * @param entityID int. The number of the molecular entity.
     * @param atom1 Atom. The first atom in the NOE atom pair.
     * @param atom2 Atom. The second atom in the NOE atom pair.
     * @param value Double. parameter value.
     * @param error Double. error value.
     * @return
     */
    public static String toStarNOEString(int idx, int listID, int entityID, Atom atom1, Atom atom2, Double value, Double error) {

        Atom[] atoms = {atom1, atom2};

        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(String.format("%-3d", idx));
        for (Atom atom : atoms) {
            int resNum = 1;
            String resName = ".";
            String nucName = ".";
            int isotope = 1;
            if (atom != null) {
                resNum = atom.getResidueNumber();
                resName = atom.getResidueName();
                nucName = atom.getName();
                switch (nucName) {
                    case "C":
                        isotope = 13;
                        break;
                    case "N":
                        isotope = 15;
                        break;
                    case "F":
                        isotope = 19;
                        break;
                    case "P":
                        isotope = 31;
                        break;
                    default:
                        break;
                }
            }
            sBuilder.append(String.format("%-3s", "."));
            sBuilder.append(String.format("%-3d", entityID));
            sBuilder.append(String.format("%-3d", entityID));
            sBuilder.append(String.format("%-6d", resNum));
            sBuilder.append(String.format("%-6d", resNum));
            sBuilder.append(String.format("%-6s", resName)); //fixme writing out chainID, not compound name (e.g. B instead of SO4) when molecule loaded from CIF
            sBuilder.append(String.format("%-4s", nucName));
            sBuilder.append(String.format("%-4s", nucName));
            sBuilder.append(String.format("%-4s", isotope));
        }
        sBuilder.append(String.format("%-8.3f", value));
        sBuilder.append(String.format("%-8.3f", error));
        sBuilder.append(String.format("%-3s", "."));
        sBuilder.append(String.format("%-3s", "."));
        for (Atom atom : atoms) {
            int resNum = 1;
            String resName = ".";
            String nucName = ".";
            if (atom != null) {
                resNum = atom.getResidueNumber();
                resName = atom.getResidueName();
                nucName = atom.getName();
            }
            sBuilder.append(String.format("%-3s", "."));
            sBuilder.append(String.format("%-6d", resNum));
            sBuilder.append(String.format("%-4s", resName));
            sBuilder.append(String.format("%-4s", nucName));
        }
        sBuilder.append(String.format("%-4s", "."));
        sBuilder.append(String.format("%-4d", listID));

        return sBuilder.toString();

    }

    /**
     * Write out the Relaxation Data (T1, T2, T1rho) sections of the STAR file.
     *
     * @param chan FileWriter. The FileWriter to use
     * @param molecule Molecule. The molecule to use
     * @param relaxDataA0 RelaxationData. The relaxation dataset.
     * @param listID int. The number of the T1/T2/T1rho/NOE block in the file.
     * @throws IOException
     * @throws InvalidMoleculeException
     */
    public static void writeRelaxation(FileWriter chan, MoleculeBase molecule, RelaxationData relaxDataA0, int listID) throws IOException, InvalidMoleculeException {
        List<Atom> atoms = molecule.getAtomArray();
        relaxTypes expType = relaxDataA0.getExpType();
        String frameName = relaxDataA0.getID();
        double field = relaxDataA0.getField();
        String coherenceType = relaxDataA0.getExtras().get("coherenceType");
        String units = relaxDataA0.getExtras().get("units");
        chan.write("    ########################################\n");
        chan.write("    #  Heteronuclear " + expType + " relaxation values  #\n");
        chan.write("    ########################################\n");
        chan.write("\n\n");
        chan.write("save_" + frameName + "\n");
        chan.write("   _Heteronucl_" + expType + "_list.Sf_category                    ");
        chan.write("heteronucl_" + expType + "_relaxation\n");
        chan.write("   _Heteronucl_" + expType + "_list.Sf_framecode                   ");
        chan.write(frameName + "\n");
        chan.write("   _Heteronucl_" + expType + "_list.Entry_ID                       ");
        chan.write(".\n"); //fixme get dynamically
        chan.write("   _Heteronucl_" + expType + "_list.ID                             ");
        chan.write(listID + "\n");
        chan.write("   _Heteronucl_" + expType + "_list.Sample_condition_list_ID       ");
        chan.write(listID + "\n");
        chan.write("   _Heteronucl_" + expType + "_list.Sample_condition_list_label    ");
        chan.write("$sample_conditions_" + listID + "\n");
        chan.write("   _Heteronucl_" + expType + "_list.Temp_calibration_method        ");
        chan.write(STAR3.quote("no calibration applied") + "\n");
        chan.write("   _Heteronucl_" + expType + "_list.Temp_control_method            ");
        chan.write(STAR3.quote("no temperature control applied") + "\n");
        chan.write("   _Heteronucl_" + expType + "_list.Spectrometer_frequency_1H      ");
        chan.write(String.valueOf(field) + "\n");
        chan.write("   _Heteronucl_" + expType + "_list." + expType + "_coherence_type              ");
        chan.write(coherenceType + "\n");
        chan.write("   _Heteronucl_" + expType + "_list." + expType + "_val_units                   ");
        chan.write(units + "\n");
        chan.write("   _Heteronucl_" + expType + "_list.Rex_units                      ");
        chan.write(".\n");
        chan.write("   _Heteronucl_" + expType + "_list.Details                        ");
        chan.write(".\n");
        chan.write("   _Heteronucl_" + expType + "_list.Text_data_format               ");
        chan.write(".\n");
        chan.write("   _Heteronucl_" + expType + "_list.Text_data                      ");
        chan.write(".\n");

        chan.write("\n");
        chan.write("   loop_\n");
        chan.write("      _Heteronucl_" + expType + "_experiment.Experiment_ID\n");
        chan.write("      _Heteronucl_" + expType + "_experiment.Experiment_name\n");
        chan.write("      _Heteronucl_" + expType + "_experiment.Sample_ID\n");
        chan.write("      _Heteronucl_" + expType + "_experiment.Sample_label\n");
        chan.write("      _Heteronucl_" + expType + "_experiment.Sample_state\n");
        chan.write("      _Heteronucl_" + expType + "_experiment.Entry_ID\n");
        chan.write("      _Heteronucl_" + expType + "_experiment.Heteronucl_" + expType + "_list_ID\n");
        chan.write("\n");

        String nmrExpType = "2D 1H-15N HSQC"; //fixme get dynamically
        String sampleLabel = "$sample_" + listID; //fixme get dynamically
        String result1 = String.format("%-2d %-7s %-7s %-9s %-2s %-2s %-2d", listID, STAR3.quote(nmrExpType), listID, sampleLabel, ".", ".", listID);
        chan.write("      " + result1 + "\n");
        chan.write("   stop_\n\n");

        String[] loopStrings = {"ID", "Assembly_atom_ID", "Entity_assembly_ID", "Entity_ID", "Comp_index_ID", "Seq_ID",
            "Comp_ID", "Atom_ID", "Atom_type", "Atom_isotope_number", "Val", "Val_err", "Resonance_ID", "Auth_entity_assembly_ID",
            "Auth_seq_ID", "Auth_comp_ID", "Auth_atom_ID", "Entry_ID", "Heteronucl_" + expType + "_list_ID"};
        if (expType.equals(relaxTypes.T2) || expType.equals(relaxTypes.T1RHO)) {
            String[] loopStrings2 = {"ID", "Assembly_atom_ID", "Entity_assembly_ID", "Entity_ID", "Comp_index_ID", "Seq_ID",
                "Comp_ID", "Atom_ID", "Atom_type", "Atom_isotope_number", expType + "_val", expType + "_val_err", "Rex_val", "Rex_err",
                "Resonance_ID", "Auth_entity_assembly_ID", "Auth_seq_ID", "Auth_comp_ID", "Auth_atom_ID", "Entry_ID", "Heteronucl_" + expType + "_list_ID"};
            loopStrings = loopStrings2;
        }
        chan.write("   loop_\n");
        for (String loopString : loopStrings) {
            chan.write("      _" + expType + "." + loopString + "\n");
        }
        chan.write("\n");

        int idx = 1;

        List<String> prevRes = new ArrayList<>();
        Iterator entityIterator = molecule.entityLabels.values().iterator();
        while (entityIterator.hasNext()) {
            Entity entity = (Entity) entityIterator.next();
            int entityID = entity.getIDNum();
            for (Atom atom : atoms) {
                List<RelaxationData> relaxDataList = atom.getRelaxationData(expType, field, null).stream()
                        .filter(r -> r.getID().contains("RING_fit")).collect(Collectors.toList());
                if (relaxDataList != null) {
                    for (RelaxationData relaxData : relaxDataList) {
                        Double value = relaxData.getValue();
                        Double error = relaxData.getError();
                        List<Double> results = new ArrayList<>();
                        results.add(value);
                        results.add(error);
                        if (expType.equals(relaxTypes.T2) || expType.equals(relaxTypes.T1RHO)) {
                            Double RexValue = ((RelaxationRex) relaxData).getRexValue();
                            Double RexError = ((RelaxationRex) relaxData).getRexError();
                            results.add(RexValue);
                            results.add(RexError);
                        }
                        String outputLine = toStarRelaxationString(idx, expType, listID, entityID, atom, results);
                        if (outputLine != null && !prevRes.contains(entityID + "." + atom.getResidueNumber())) {
                            chan.write("      " + outputLine + "\n");
                            prevRes.add(entityID + "." + atom.getResidueNumber());
                            idx++;
                        }
                    }
                }
            }
        }

        chan.write("   stop_\n");
        chan.write("save_\n\n");

    }

    /**
     * Write the data lines in the Relaxation Data (T1, T2, T1rho) blocks of the
     * STAR file.
     *
     * @param idx int. The line index
     * @param expType relaxTypes. The experiment type: T1, T2, T1rho.
     * @param listID int. The number of the T1/T2/T1rho block in the file.
     * @param entityID int. The number of the molecular entity.
     * @param atom Atom. The atom in the molecule.
     * @param results List<Double>. The relaxation and error values: {value,
     * error, RexValue, RexError}.
     * @return
     */
    public static String toStarRelaxationString(int idx, relaxTypes expType, int listID, int entityID, Atom atom, List<Double> results) {

        int resNum = idx;
        String resName = ".";
        String oneLetter = ".";
        String nucName = ".";
        int isotope = 1;
        if (atom != null) {
            resNum = atom.getResidueNumber();
            resName = atom.getResidueName();
            oneLetter = String.valueOf(((Residue) atom.entity).getOneLetter());
            nucName = atom.getName();
            switch (nucName) {
                case "C":
                    isotope = 13;
                    break;
                case "N":
                    isotope = 15;
                    break;
                case "F":
                    isotope = 19;
                    break;
                case "P":
                    isotope = 31;
                    break;
                default:
                    break;
            }
        }

        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(String.format("%-3d", idx));
        sBuilder.append(String.format("%-3s", "."));
        sBuilder.append(String.format("%-3d", entityID));
        sBuilder.append(String.format("%-3d", entityID));
        sBuilder.append(String.format("%-6d", resNum));
        sBuilder.append(String.format("%-6d", resNum));
        sBuilder.append(String.format("%-6s", resName)); //fixme writing out chainID, not compound name (e.g. B instead of SO4) when molecule loaded from CIF
        sBuilder.append(String.format("%-4s", nucName));
        sBuilder.append(String.format("%-4s", nucName));
        sBuilder.append(String.format("%-4s", isotope));
        results.forEach((value) -> {
            if (value != null) {
                sBuilder.append(String.format("%-8.3f", value));
            } else {
                sBuilder.append(String.format("%-3s", "."));
            }
        });
        sBuilder.append(String.format("%-3s", "."));
        sBuilder.append(String.format("%-3s", "."));
        sBuilder.append(String.format("%-6d", resNum));
        sBuilder.append(String.format("%-4s", oneLetter));
        sBuilder.append(String.format("%-4s", nucName));
        sBuilder.append(String.format("%-4s", "."));
        sBuilder.append(String.format("%-4d", listID));

        return sBuilder.toString();

    }

    public static void writeAll(String fileName) throws IOException, ParseException, InvalidPeakException, InvalidMoleculeException {
        try (FileWriter writer = new FileWriter(fileName)) {
            writeAll(writer);
        }
    }

    public static void writeAll(File file) throws IOException, ParseException, InvalidPeakException, InvalidMoleculeException {
        try (FileWriter writer = new FileWriter(file)) {
            writeAll(writer);
        }
    }

    public static void writeAll(FileWriter chan) throws IOException, ParseException, InvalidPeakException, InvalidMoleculeException {

        Date date = new Date(System.currentTimeMillis());
        chan.write("    ######################################\n");
        chan.write("    # Saved " + date.toString() + " #\n");
        chan.write("    ######################################\n");
        MoleculeBase molecule = MoleculeFactory.getActive();
        if (molecule != null) {
            writeMoleculeSTAR3(chan, molecule, 1);
        }
        // fixme Dataset.writeDatasetsToSTAR3(channelName);
        Iterator iter = PeakList.iterator();
        PeakWriter peakWriter = new PeakWriter();
        while (iter.hasNext()) {
            PeakList peakList = (PeakList) iter.next();
            peakWriter.writePeaksSTAR3(chan, peakList);
        }

        ResonanceFactory resFactory = PeakList.resFactory();

        resFactory.writeResonancesSTAR3(chan);
        if (molecule != null) {
            int ppmSetCount = molecule.getPPMSetCount();
            for (int iSet = 0; iSet < ppmSetCount; iSet++) {
                writeAssignmentsSTAR3(chan, iSet);
            }
            CoordinateSTARWriter.writeToSTAR3(chan, molecule, 1);
            int setNum = 1;

            for (ConstraintSet cSet : molecule.getMolecularConstraints().noeSets()) {
                if (cSet.getSize() > 0) {
                    ConstraintSTARWriter.writeConstraintsSTAR3(chan, cSet, setNum++);
                }
            }
            setNum = 1;
            for (ConstraintSet cSet : molecule.getMolecularConstraints().angleSets()) {
                if (cSet.getSize() > 0) {
                    ConstraintSTARWriter.writeConstraintsSTAR3(chan, cSet, setNum++);
                }
            }
        }
        PeakPathWriter pathWriter = new PeakPathWriter();
        int iPath = 0;
        for (PeakPaths peakPath : PeakPaths.get()) {
            pathWriter.writeToSTAR3(chan, peakPath, iPath + 1);
            iPath++;
        }
        if (molecule != null) {
            Collection<RelaxationData> molRelaxData = RelaxationData.getRelaxationData(molecule.getAtomArray());
            Set<relaxTypes> expTypes = RelaxationData.getExpTypes(molecule);
            if (expTypes != null) {
                for (relaxTypes expType : expTypes) {
                    int listID = 1;
                    List<RelaxationData> relaxDataList = molRelaxData.stream()
                            .filter(d -> d.getID().contains("RING_fit") && d.getExpType().equals(expType))
                            .collect(Collectors.toList());
                    if (!relaxDataList.isEmpty()) {
                        if (relaxDataList.get(0).getExpType().equals(relaxTypes.NOE)) {
                            writeNOE(chan, molecule, relaxDataList.get(0), listID);
                        } else {
                            writeRelaxation(chan, molecule, relaxDataList.get(0), listID);
                        }
                        listID++;
                    }
                }
            }
        }
    }

}
