/*
 *  Copyright (c) 2014 Harvard University and the persons
 *  identified as authors of the code.  All rights reserved. 
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 * 
 * 	.    Redistributions of source code must retain the above copyright
 * 		 notice, this list of conditions and the following disclaimer.
 * 
 * 	.    Redistributions in binary form must reproduce the above copyright
 * 		 notice, this list of conditions and the following disclaimer in the
 * 		 documentation and/or other materials provided with the distribution.
 * 
 * 	.    Neither the name of Harvard University, nor the names of specific
 * 		 contributors, may be used to endorse or promote products derived from
 * 		 this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. *      
 */      
package edu.harvard.integer.server.parser.mibparser.moduleloader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.hibernate.type.descriptor.sql.BitTypeDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibLoaderLog;
import net.percederberg.mibble.MibLoaderLog.LogEntry;
import net.percederberg.mibble.MibSymbol;
import net.percederberg.mibble.MibType;
import net.percederberg.mibble.MibTypeSymbol;
import net.percederberg.mibble.MibValue;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.snmp.SnmpAccess;
import net.percederberg.mibble.snmp.SnmpIndex;
import net.percederberg.mibble.snmp.SnmpModuleIdentity;
import net.percederberg.mibble.snmp.SnmpObjectType;
import net.percederberg.mibble.snmp.SnmpRevision;
import net.percederberg.mibble.snmp.SnmpTextualConvention;
import net.percederberg.mibble.type.BitSetType;
import net.percederberg.mibble.type.IntegerType;
import net.percederberg.mibble.type.ObjectIdentifierType;
import net.percederberg.mibble.value.ObjectIdentifierValue;
import edu.harvard.integer.common.exception.CommonErrorCodes;
import edu.harvard.integer.common.exception.IntegerException;
import edu.harvard.integer.common.snmp.MIBImportInfo;
import edu.harvard.integer.common.snmp.MIBImportResult;
import edu.harvard.integer.common.snmp.MaxAccess;
import edu.harvard.integer.common.snmp.SNMP;
import edu.harvard.integer.common.snmp.SNMPIndex;
import edu.harvard.integer.common.snmp.SNMPModule;
import edu.harvard.integer.common.snmp.SNMPModuleHistory;
import edu.harvard.integer.common.snmp.SNMPTable;
import edu.harvard.integer.common.snmp.SnmpEnumList;
import edu.harvard.integer.common.snmp.SnmpEnumValue;
import edu.harvard.integer.common.snmp.SnmpSyntax;
import edu.harvard.integer.common.type.displayable.DisplayableInterface;
import edu.harvard.integer.common.type.displayable.NonLocaleErrorMessage;
import edu.harvard.integer.server.parser.mibparser.MibParser;
import edu.harvard.integer.server.parser.mibparser.SNMPModuleCache;


/**
 * The Class MibbleParser is used to load MIB module into the system.
 * It implements the functions specified on MibParser.
 *
 * @author dchan
 */
public class MibbleParser implements MibParser{
	
	/** The Mib resource dir. */
	public static String MibResourceDir = "mibs/ietf/";
	
	/** The Tmp file prefix to hold the temp module files when loading a new module into the system.  */
	public static String TmpFilePrefix = "tmp";
	
	
	/** The mib repository location which store all the MIB using by the system. */
	private File mibLocation;	
	
	/** The singleton instance */
	private static MibbleParser source = null;
	
	/** The mib loader. */
	private MibLoader mibLoader = new MibLoader();
	
	private Logger logger = LoggerFactory.getLogger(MibbleParser.class);
	
	/**
	 * Instantiates a new mibble parser.
	 *
	 * @throws IntegerException the integer exception
	 */
	private MibbleParser() throws IntegerException {
				
		mibLocation =  new File("installMibs");
		if ( !makeDirIfNotExist("installMibs") ) {
			throw new IntegerException(null, CommonErrorCodes.DirectoryNotValid);
		
		} 	
	}

	private boolean makeDirIfNotExist(String path) {
		File file = new File(path);
		if (!file.isDirectory()) {
			if (file.mkdir())
				return true;
			else {
				System.err.println("Error Unable to create directory " + path);
				return false;
			}
		}
			
		return true;
	}
	
	/**
	 * Used to get module map from the existing repository.  
	 *
	 * @return the module map from repository.  The key is the module name.
	 * @throws CapabilitySetterException the capability setter exception
	 */
	public HashMap<String, SNMPModuleCache> getModuleMapFromRepository() throws IntegerException {
		
		HashMap<String, SNMPModuleCache>  snmpCache = new HashMap<>();
		mibLoader.removeAllDirs();		
		/**
		 * MIBS are containing on under two directories.  One is called ietf which contain common MIBS
		 * another one is called vendor which holds vendor MIBs.
		 */
		mibLoader.addDir(mibLocation);
		try {
			loadFile(mibLocation);
		}
		catch ( Exception e ) {
			
			throw new IntegerException(e, CommonErrorCodes.RunTimeError, 
					new DisplayableInterface[] { new NonLocaleErrorMessage(e.getMessage()) } );
		}
	    Mib[] mibs = mibLoader.getAllMibs();
	    for ( Mib mib : mibs ) {
	    	
	    	SNMPModuleCache moduleCache = snmpCache.get(mib.getName());
	    	if ( moduleCache == null ) {
	    		
	    		SNMPModule snmpModule = new SNMPModule();
	    		
	    		MibValue ident = null;
	    		snmpModule.setName(mib.getName());
	    		if ( mib.getRootSymbol() != null ) {
	    			ident = mib.getRootSymbol().getValue();
	    		}
	    		else {	    		
	    			continue;
	    		}
	    		
	    		String oid = ident.toObject().toString();
	    		snmpModule.setOid(oid);
	    		
	    		moduleCache = new SNMPModuleCache(snmpModule);
	    	}
	    	boolean containInfo = fillUpMibInfo(moduleCache, mib);
	    	if ( containInfo ) {
	    		snmpCache.put(mib.getName(), moduleCache);
	    	}
	    }
	    return snmpCache;
	}
	
	
	
	/**
	 * Fill the module information which includes table and scalar to "moduleCache".
	 *
	 * @param moduleCache the module cache
	 * @param mib the mib
	 * @return true, if successful
	 */
	@SuppressWarnings("unchecked")
	public boolean fillUpMibInfo( SNMPModuleCache moduleCache, Mib mib )  
	{
		List<SNMPTable>  tblList = new ArrayList<>();
    	List<SNMP>  scaleList = new ArrayList<>();
    	List<SNMP>  objectList = new ArrayList<>();
		
		Collection<MibSymbol> ss = mib.getAllSymbols();
    	for (MibSymbol s : ss)
    	{
    		if (s instanceof MibValueSymbol)
    		{
    			MibValueSymbol vs = (MibValueSymbol) s;
    			/**
    			 * Search for node which is mib "entry" and create an table on
    			 * the repository
    			 * 
    			 */
    			if (vs.isTableRow())
    			{
    				SNMPTable snmpTbl = new SNMPTable();    				
    				/**
    				 * Table oids list
    				 */
    				List<SNMP> oids = new ArrayList<SNMP>();  				
    				snmpTbl.setTableOids(oids);
    				SnmpObjectType mt =  (SnmpObjectType) vs.getType();
                    ArrayList<SnmpIndex> sis =  mt.getIndex();
    				
    				if ( sis.size() > 0 ) {
    					
    					/**
    					 * Table index.
    					 */
    					List<SNMP> index = new ArrayList<>();
    					snmpTbl.setIndex(index);
    				}    		
    				
    				snmpTbl.setName(vs.getName());
    				snmpTbl.setOid(vs.getValue().toString());
    				SnmpObjectType snmpType = (SnmpObjectType) vs.getType();
    				snmpTbl.setDescription(snmpType.getDescription());
    				snmpTbl.setMaxAccess(MaxAccess.NotAccessible);
    				

    				MibValueSymbol[] avss = vs.getChildren();
    				for (MibValueSymbol avs : avss)
    				{
    					/**
    					 * Only card if it is accessable.
    					 */
    					snmpType = (SnmpObjectType) avs.getType();    					
    					if (snmpType.getAccess().canRead() || snmpType.getAccess().canWrite() )
    					{
    						ObjectIdentifierValue obj = (ObjectIdentifierValue) avs.getValue();
    						SNMP snmp = createSNMP( obj, snmpType, false );
    					    oids.add(snmp);
    					}
    				}
    				
                    for ( int i=0; i<sis.size(); i++ ) {
    					
    					SnmpIndex si = sis.get(i);
    					SNMP snmp = createSNMPIndex(si);
  					    snmpTbl.getIndex().add(snmp);
    					
    				}  

    				
    				tblList.add(snmpTbl);
    			} 
    			else if (vs.isScalar())
    			{
    				SnmpObjectType snmpType = (SnmpObjectType) vs.getType();
    				if ( snmpType.getAccess().canRead() || snmpType.getAccess().canWrite() ) 
    				{
    					ObjectIdentifierValue obj = (ObjectIdentifierValue) vs.getValue();
    					SNMP snmp = createSNMP( obj, snmpType, true );
    					
    					scaleList.add(snmp);				    	
    				}
    			}
    			else if ( vs.getType() instanceof SnmpModuleIdentity ) {
    				
    				SnmpModuleIdentity snmpModule = (SnmpModuleIdentity) vs.getType();
    				moduleCache.getModule().setDescription(snmpModule.getDescription());
    				
    				String ds = snmpModule.getLastUpdated();
    				if ( ds != null ) {
    					Calendar c = decodeSnmpDate(ds);  
    					moduleCache.getModule().setLastUpdated(c.getTime());
    				}
    				List<SNMPModuleHistory> moduleHistories = moduleCache.getHistory();
    				if ( moduleHistories == null ) {
    					
    					moduleHistories = new ArrayList<>();
    					moduleCache.setHistory(moduleHistories);
    				}
    				if ( snmpModule.getRevisions() != null ) {
    					for ( int i=0; i<snmpModule.getRevisions().size(); i++ ) {
        					SnmpRevision sr = (SnmpRevision) snmpModule.getRevisions().get(i);
        					SNMPModuleHistory moduleHistory = new SNMPModuleHistory();
            				moduleHistories.add(moduleHistory);

            				moduleHistory.setDescription(sr.getDescription());
            				try {
                				ds = sr.getValue().toString();
                				Calendar c = decodeSnmpDate(ds);
                				moduleHistory.setDate(c.getTime());
            				}
            				catch ( Exception e ) {}
        				}
    				}    				
    			}
    			else if ( vs.getType() instanceof ObjectIdentifierType ) {
    				
    				SNMP snmp = new SNMP();
    				snmp.setName(vs.getName());
    				snmp.setOid(vs.getValue().toString());
    				snmp.setMaxAccess(MaxAccess.NotAccessible);
    				snmp.setComment(vs.getComment());
    				
    				objectList.add(snmp);	
    			}
    		}
    	}
    	if ( tblList.size() > 0 || scaleList.size() > 0 || objectList.size() > 0 ) {
    		
	    	for ( SNMPTable tbl : tblList ) {
	    		moduleCache.getTbllist().add(tbl);
	    	}
	    	
	    	for ( SNMP snmp : scaleList ) {
	    		moduleCache.getScalelist().add(snmp);
	    	}
	    	
	    	if ( objectList.size() > 0 ) {
	    	    moduleCache.setObjectIdentifiers(objectList);
	    	}
	        return true;	
    	}
		return false;
	}
	
	
	/**
	 * This method is used to decode SNMP date into Calendar.
	 * 
	 * @param ds
	 * @return
	 */
	private Calendar decodeSnmpDate( String ds ) {
		
		String y = null;
		String m = null;
		String d = null;
		String h = null;
		String mm = null;
		
		int i = 2;
		if ( ds.length() == 11 ) {
			y = "19" + ds.substring(0, i);
		}
		else {
			i = 4;
			y = ds.substring(0, 4);
		}    					
		m = ds.substring(i, i+2);
		i = i + 2;
		d = ds.substring(i, i+2);
		i = i + 2;
		h = ds.substring(i, i+2);
		i = i + 2;
		mm = ds.substring(i, i+2);
		
		Calendar c = Calendar.getInstance();
		c.set(Integer.parseInt(y), Integer.parseInt(m), Integer.parseInt(d), 
				Integer.parseInt(h), Integer.parseInt(mm));
		
		return c;
	}
	
	
	/**
	 * Return a SNMPModuleCache which contains table and scalar information.
	 * If the Module contains no table and scalar information, return null.
	 *
	 * @param mibInfo the mib info
	 * @return SNMPModuleCache contains entry and scalar information.
	 * @throws Exception the exception
	 */
	public SNMPModuleCache generateModule( MibProcessingInfo mibInfo ) throws Exception {
		    
		Mib mib = mibLoader.getMib(mibInfo.fileName);
    	SNMPModule snmpModule = new SNMPModule();
    	
		MibValue ident = null;
		snmpModule.setName(mib.getName());
		if ( mib.getRootSymbol() != null ) {
			ident = mib.getRootSymbol().getValue();
		}
		else {	    		
			return null;
		}		
		String oid = ident.toObject().toString();
		snmpModule.setOid(oid);
		
		SNMPModuleCache moduleCache = new SNMPModuleCache(snmpModule);
		fillUpMibInfo(moduleCache, mib);		
		return moduleCache;
	}
	
	
	/**
	 * Load file.
	 *
	 * @param dirf the dirf
	 * @throws Exception the exception
	 */
	private void loadFile( File dirf ) throws Exception {
		
		for ( File f : dirf.listFiles() ) {
			
			if ( f.isHidden() ) {
				continue;
			}
 	    	try {
				Mib mib = mibLoader.load(f);
				if ( !mib.getName().equalsIgnoreCase(f.getName()) ) {
					System.out.println("Wrong name .. " + mib.getName() + " " + f.getName() );
    				mibLoader.unload(f.getName());
    				File goodNameFile = new File(f.getAbsolutePath(), mib.getName());
    					
    				f.renameTo(goodNameFile);
    				mibLoader.load(goodNameFile.getName());
				}
			} 
 	    	catch (IOException e) {
				throw e;
			} 
 	    	catch (MibLoaderException e) {
 	    		
 	    		MibLoaderLog mlog = e.getLog();
 	    		Iterator it = mlog.entries();
 	    		while ( it.hasNext() ) {
 	    			
 	    			LogEntry log = (LogEntry) it.next();
 	    			if ( log.isError() ) 
 	    			{
 	    				System.out.println(log.getMessage());
 	    			}
 	    		}
			}
 	    }
	}
	
	
	/**
	 * Gets the single instance of MibbleParser.
	 *
	 * @return single instance of MibbleParser
	 * @throws IntegerException the integer exception
	 */
	public static MibbleParser getInstance() throws IntegerException{
		
		synchronized (MibbleParser.class) {
			if ( source == null ) {				
				source = new MibbleParser();
			}
		}
		return source;		
	}
	
	
	
	/* (non-Javadoc)
	 * @see edu.harvard.integer.capabilitySetter.snmp.MibParser#getSupportVendorModuleNames()
	 */
	@Override
	public List<String>  getSupportVendorModuleNames() throws IntegerException {
		
	    ClassLoader  loader = mibLoader.getClass().getClassLoader();
	    CodeSource src = mibLoader.getClass().getProtectionDomain().getCodeSource();
	    List<String> list = new ArrayList<String>();

	    try {
	    	if( src != null ) {
		        URL jar = src.getLocation();
		        ZipInputStream zip = new ZipInputStream( jar.openStream());
		        ZipEntry ze = null;

		        while( ( ze = zip.getNextEntry() ) != null ) {
		            String entryName = ze.getName();
		            
		            if( entryName.startsWith(MibResourceDir) && loader.getResource(entryName) != null ) {
		            	
		            	String mibName = entryName.replaceAll(MibResourceDir, "");
		            	if ( mibName.length() > 0 ) {
		            		 list.add(mibName);
		            	}
		            }
		        }
		     }	
	     }
	     catch ( IOException e ) {
	    	 
	     }
		 return list;
	}

	
	
	/**
	 * Gets the mib.
	 *
	 * @param moduleName the module name
	 * @return the mib
	 * @throws Exception the exception
	 */
	public Mib getMib( String moduleName ) throws Exception {
		
		Mib m = mibLoader.getMib(moduleName);
		if ( m == null ) {
			m = mibLoader.load(moduleName);
		}		
		return m;
	}


	/* (non-Javadoc)
	 * @see edu.harvard.integer.capabilitySetter.snmp.MibParser#loadModule(java.io.File)
	 */
	@Override
	public void loadModule(File moduleFile) throws IntegerException {
		
		try {
			mibLoader.load(moduleFile);		    
		} 
		catch ( IOException ioe ) {
			
			throw new IntegerException(ioe, CommonErrorCodes.IOError, 
					new DisplayableInterface[] { new NonLocaleErrorMessage(ioe.getMessage()) } );
		}
		catch ( MibLoaderException e) {
			
			List<String> errors = new ArrayList<>();
			
	    	MibLoaderLog mlog = e.getLog();
	    	Iterator it = mlog.entries();        	    		
	    	boolean exist = false;
	    		
	    	while ( it.hasNext() ) {
	    			
	    		LogEntry log = (LogEntry) it.next();
	    		if ( log.isError() ) {
	    				
	    			String errStr = log.getMessage() + " on line: " + log.getLineNumber();
	    				
	    			for ( String s : errors ) {
	    				if ( s.equalsIgnoreCase(errStr) ) {
	    					exist = true;
	    				}
	    			}
	    			if ( !exist ) {
	    				errors.add(errStr);
	    			}         	    				
	    		}
	    	}
	    	StringBuffer sb = new StringBuffer();
	    	for ( String s : errors ) {
	    		
	    		sb.append(s + "\n");
	    	}	    	
			throw new IntegerException(e, CommonErrorCodes.ParserError, 
					new DisplayableInterface[] { new NonLocaleErrorMessage(sb.toString()) } );
		}
	}


	/* (non-Javadoc)
	 * @see edu.harvard.integer.capabilitySetter.snmp.MibParser#getLoadedSNMPObjects()
	 */
	@Override
	public HashMap<String, SNMP> getLoadedSNMPObjects() {
		// TODO Auto-generated method stub
		return null;
	}




	/* (non-Javadoc)
	 * @see edu.harvard.integer.capabilitySetter.snmp.MibParser#getSupportCommonModuleNames()
	 */
	@Override
	public List<String> getSupportCommonModuleNames() throws IntegerException {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see edu.harvard.integer.capabilitySetter.snmp.MibParser#importMIB(edu.harvard.integer.common.snmp.MIBImportInfo[], boolean)
	 */
	@Override
	public MIBImportResult[] importMIB( MIBImportInfo[] mibinfos,
			                                boolean useExisting) throws IntegerException {
	
		File[] files = mibLocation.listFiles();
		mibLoader.removeAllDirs();	
		
		if ( useExisting ) {
			createTmpDirs();
		}
		List<MibProcessingInfo> processingList = new ArrayList<>();
		
		try {			
			List<MIBImportResult> importReturn = new ArrayList<>();	
			
			loadMib(files, useExisting, mibinfos, importReturn, processingList);			
    		return importReturn.toArray(new MIBImportResult[importReturn.size()]);
    		
		}
		catch ( Exception e ) {
			
			e.printStackTrace();
			
			// If an exception occurs in here.  Throw the exception before that is one need to take care of before
			// the parser error.
			String message = null;
			if ( e.getMessage() == null ) {
	
			}
			else {				
				message = e.getMessage();
			}
			throw new IntegerException(e, CommonErrorCodes.RunTimeError,  
					new DisplayableInterface[] { new NonLocaleErrorMessage(message) });
		}
		finally {
			
			/**
			 * Remove any module in the import list which fails to imported.
			 */
			
			 List<SNMPModuleCache> uploadModules = new ArrayList<>();
			 for ( MibProcessingInfo mibInfo : processingList ) {
				 
				 String mibFilefulltmp = getFullTmpName(TmpFilePrefix + mibInfo.fileName);
				 if ( !mibInfo.importSuccess ) {
					 String mibFilefull = getFullName(mibInfo.fileName);
	    		     File f = new File(mibFilefull);
	    		     f.delete();	    			 
	    		     
	    		     File f2 = new File(mibFilefulltmp);
	    		     if ( f2.exists() ) {
		    		     f = new File(mibFilefull);
		    		     f2.renameTo(f);	 
	    		     }   		     
				 }
				 else {
					 try {
						SNMPModuleCache module =  generateModule(mibInfo);
						if ( module != null ) {
							uploadModules.add(module);
						}
					} 
					 catch (Exception e) {
						 
						 System.out.println(mibInfo.fileName + " " + e.getMessage());
						 
						 // We are not supposed to get any Exception in here since those MIBs are already loaded.
						 //e.printStackTrace();
					}
				 }
				 File f2 = new File(mibFilefulltmp);
    		     if ( f2.exists() ) {
    		    	 f2.delete();
    		     }		 
			 }
		}
	}
	
	/**
	 * Creates SNMP object based on Mibble  ObjectIdentifierValue and SnmpObjectType
	 *
	 * @param obj the obj
	 * @param snmpType the snmp type
	 * @return the snmp
	 */
	private SNMP createSNMP( ObjectIdentifierValue obj, SnmpObjectType snmpType, boolean isScalar ) {
		
		MibType oType = (MibType) obj.getSymbol().getType();
		
	    SNMP snmp = new SNMP();
	    snmp.setScalarVB(isScalar);
	    
	    if ( obj.toObject().toString().indexOf("1.3.6.1.4.1.9.9.23.1.3.6") >= 0 ) {
	    	System.out.println(obj.toObject().toString());
	    }

	    if ( snmpType.getSyntax().getReferenceSymbol() != null && snmpType.getSyntax().getReferenceSymbol().getType() instanceof SnmpTextualConvention )
	    {
	    	if ( snmpType.getSyntax() instanceof IntegerType ) {
	    		 IntegerType it = (IntegerType) snmpType.getSyntax();
	    		 MibValueSymbol[] mvss = it.getAllSymbols();
	    		 if ( mvss != null ) {
	    			 SnmpEnumList sel = new SnmpEnumList();
	    			 sel.setValues(new ArrayList<SnmpEnumValue>());
	    			 for ( MibValueSymbol mvs : mvss ) {
		    			
	    				 SnmpEnumValue sev = new SnmpEnumValue();
	    				 sev.setName(mvs.getName());
	    				 sev.setValue(Integer.parseInt(mvs.getValue().toString()));	    				 
	    				 sel.getValues().add(sev);
	    				 
		    			 System.out.println("" + mvs.getName() + " " + mvs.getValue().toString()); 
		    		 }
	    			 snmp.setSyntax(sel);
	    		 }
	    	}
	    	else if ( snmpType.getSyntax() instanceof BitSetType ) {
	    		BitSetType bt = (BitSetType)snmpType.getSyntax();
	    		MibValueSymbol[] mvss = bt.getAllSymbols();
	    		 if ( mvss != null ) {
	    			 SnmpEnumList sel = new SnmpEnumList();
	    			 sel.setValues(new ArrayList<SnmpEnumValue>());
	    			 for ( MibValueSymbol mvs : mvss ) {
		    			
	    				 SnmpEnumValue sev = new SnmpEnumValue();
	    				 sev.setName(mvs.getName());
	    				 sev.setValue(Integer.parseInt(mvs.getValue().toString()));	    				 
	    				 sel.getValues().add(sev);
	    				 
		    			 System.out.println("" + mvs.getName() + " " + mvs.getValue().toString()); 
		    		 }
	    			 snmp.setSyntax(sel);
	    		 }
	    	}
	    	snmp.setTextualConvetion(snmpType.getSyntax().getReferenceSymbol().getName());
	    }
	    snmp.setName(obj.getName());
	    
	    snmp.setOid(obj.toObject().toString());
	    snmp.setDescription(snmpType.getDescription());
	    snmp.setComment(obj.getSymbol().getComment());
	     					    
	    MaxAccess access = null;
	    if ( snmpType.getAccess() == SnmpAccess.READ_ONLY ) {
	    	access = MaxAccess.ReadOnly;
	    }
	    else if ( snmpType.getAccess() == SnmpAccess.READ_CREATE ) {
	    	access = MaxAccess.ReadWrite;
	    }
	    else if ( snmpType.getAccess() == SnmpAccess.READ_WRITE ) {
	    	access = MaxAccess.ReadWrite;
	    }
	    else if ( snmpType.getAccess() == SnmpAccess.WRITE_ONLY ) {
	    	access = MaxAccess.WriteOnly;
	    }
	    snmp.setMaxAccess( access);
	    
	    if ( oType instanceof SnmpObjectType ) {
	    	snmp.setUnits( ((SnmpObjectType) oType).getSyntax().getName() );
	    	
	    	MibTypeSymbol ms =  ((SnmpObjectType) oType).getSyntax().getReferenceSymbol();
	    	if ( ms != null && ms.getType() instanceof SnmpTextualConvention ) {
	    		snmp.setTextualConvetion(ms.getName()); 
	    	}
	    }
	    else {
	    	System.out.println("Not an snmpObjectType .... " + oType.getClass().getName() );
	    }
	    return snmp;
	}

	
	/**
	 * 
	 * Create an index SNNP object based on Mibble SnmpIndex.
	 * For the time being, the index implied information is ignored, we may need it if
     * we are using the index information to construct the instance oid.
	 * 
	 */
	private SNMPIndex createSNMPIndex( SnmpIndex si ) {
		
		SNMPIndex snmp = new SNMPIndex();	  
		ObjectIdentifierValue mv = (ObjectIdentifierValue) si.getValue();
		if ( mv != null ) {
			snmp.setName(mv.getName());
			snmp.setOid( mv.toObject().toString());
			MibValueSymbol ms =  mv.getSymbol();
			MibType mt = ms.getType();
			if ( mt instanceof SnmpObjectType ) {
				snmp.setUnits( ((SnmpObjectType) mt).getSyntax().getName());
			}
		}
		else {
			snmp.setUnits(si.getType().getName());
			snmp.setName(si.getType().getName());
		}
		
		snmp.setImplied(si.isImplied());
	    return snmp;
	}
	
	
	/**
	 * Load mib.
	 *
	 * @param files the files
	 * @param useExisting the use existing
	 * @param mibinfos the mibinfos
	 * @param importReturn the import return
	 * @param processingList the processing list
	 */
	public void loadMib( File[] files, 
			                                boolean useExisting, MIBImportInfo[] mibinfos,
			                                List<MIBImportResult> importReturn,
			                                List<MibProcessingInfo> processingList ) {
		
		
		List<MibProcessingInfo> importList =  skipExistingFile(mibinfos, useExisting );
		for ( MibProcessingInfo processInfo : importList ) {
			processingList.add(processInfo);
		}
		
        for ( MibProcessingInfo importMib : importList ) {
        	
        	Writer writer = null;
        	File mibFilefull = null;
			try {
				
				String fileName = getFullMIBName(importMib.fileName,  importMib.importInfo.getMib());	
				if ( fileName != null ) {
					mibFilefull = new File(fileName);
					FileOutputStream outputStream = new FileOutputStream(mibFilefull);	
				    writer = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
				    writer.write(importMib.importInfo.getMib());
				    importMib.fileName = mibFilefull.getName();
				    importMib.importSuccess = true;
				}
				else {
					
					importMib.importSuccess = false;
					System.out.println("Skip this file " + importMib.fileName + " It seems to be not a mib file");
				}
			} 
			catch (IOException ex) {
			 
				// Do nothing and continue;
				importMib.importSuccess = false;
				logger.error("Error loading " + mibFilefull + " Error " + ex);
			} 
			finally {
			   try {writer.close();} catch (Exception ex) {}
			}        	
        }
        mibLoader.addDir(mibLocation);
        for ( MibProcessingInfo importMib : importList ) {
        	
        	if ( importMib.importSuccess ) {
        		
        		importMib.importSuccess = false;
        		try {
        				
        			String fileName = getFullName(importMib.fileName);
        			Mib mib = mibLoader.load(new File(fileName));
        			if ( !mib.getName().equalsIgnoreCase(importMib.fileName)) {
        					
        				mibLoader.unload(importMib.fileName);
        				File junkNameFile = new File(getFullName(importMib.fileName));
        				File goodNameFile = new File(getFullName(mib.getName()));
        					
        				junkNameFile.renameTo(goodNameFile);
        				mibLoader.load(goodNameFile.getName());
        				importMib.fileName = goodNameFile.getName();
        			} 
    				
    				/**
    				 * At this point, the module can be mark as successful loaded.
    				 */
    				importMib.importSuccess = true;
     	    		try {
						SNMPModuleCache snmpCache = generateModule(importMib);
						
						if ( snmpCache != null ) {
							
							MIBImportResult mResult = new MIBImportResult();
		     	    		importReturn.add(mResult);
		     	    		
		     	    		mResult.setName(importMib.importInfo.getName());
		     	    		mResult.setMib(importMib.importInfo.getMib());
							mResult.setSnmpTable(snmpCache.getTbllist());
							mResult.setSnmpScalars(snmpCache.getScalelist());
							mResult.setObjectIdentifiers(snmpCache.getObjectIdentifiers());
							mResult.setModule(snmpCache.getModule());
							mResult.setHistory(snmpCache.getHistory());
						}
					} 
     	    		catch (Exception e) {
     	    			// It should not have any exception at this point since
     	    			// those MIB being loaded.
					}
    				
    			}
    			catch (IOException e) {
    				logger.error("IO error on module " + " " + importMib.fileName + " Error " + e.toString());
    				
    			} 
        		
     	    	catch (MibLoaderException e) {
     	    		
     	    		MIBImportResult mResult = new MIBImportResult();
     	    		importReturn.add(mResult);
     	    		
     	    		mResult.setName(importMib.importInfo.getName());
     	    		mResult.setMib(importMib.importInfo.getMib());
     	    		
     	    		List<String> errors = new ArrayList<>();
     	    		MibLoaderLog mlog = e.getLog();
     	    		Iterator it = mlog.entries();        	    		
     	    		boolean exist = false;
     	    		
     	    		while ( it.hasNext() ) {
     	    			
     	    			LogEntry log = (LogEntry) it.next();
     	    			if ( log.isError() ) {
     	    				
     	    				String errStr = log.getMessage() + " on line: " + log.getLineNumber() + " on " + importMib.fileName;
     	    				for ( String s : errors ) {
     	    					if ( s.equalsIgnoreCase(errStr) ) {
     	    						exist = true;
     	    					}
     	    				}
     	    				if ( !exist ) {
     	    					errors.add(errStr);
     	    				}         	    				
     	    			}
     	    		}
     	    		
     	    		String[] errs  = (String[]) errors.toArray(new String[0]);
     	    		mResult.setErrors(errs);
    			}
        	}
        }
		
	}
	
	
	
	
	/**
	 * Used to skip MIB which already exist in the repository.
	 *
	 * @param mibFiles the mib files
	 * @param replaceExisting the replace existing
	 * @param isCommon the is common
	 * @return  -- A list of file ready for import.
	 */
	private List<MibProcessingInfo>  skipExistingFile( MIBImportInfo[] mibFiles, boolean replaceExisting ) {
		
		List<MibProcessingInfo> finalList = new ArrayList<>();
		File[] mibs = mibLocation.listFiles();
		
		
		for ( MIBImportInfo mInfo : mibFiles ) {
			
			String mibName = mInfo.getFileName();			
			boolean exist = false;
			for ( File f : mibs ) {
				
				if ( mibName.equalsIgnoreCase(f.getName()) ) {
					exist = true;
				}
			}
			if ( exist && !replaceExisting ) {
			    continue;
			}
			
			if ( replaceExisting && exist) {
				/**
				 * Rename the existing file.  The existing file later on will  be cleanup
				 * if the import file is good or reinstall if the import file is not good.
				 */
				File file = new File(getFullName(mibName));
				File file2 = new File(getFullTmpName(TmpFilePrefix + mibName));
				
				file.renameTo(file2);
			}
			
			MibProcessingInfo fileInfo = new MibProcessingInfo(mInfo);
			fileInfo.fileName = mibName;
			fileInfo.isCommon = mInfo.isStandardMib();
			
			finalList.add(fileInfo);			
		}
		return finalList;
	}
	
	
	/**
	 * Gets the full name.
	 *
	 * @param fileName the file name
	 * @param isCommon the is common
	 * @return the full name
	 */
	public String getFullName(String fileName) {
		return mibLocation.getAbsolutePath() + File.separator + fileName;
	}
	
	
    /**
     * Gets the full tmp name.
     *
     * @param fileName the file name
     * @param isCommon the is common
     * @return the full tmp name
     */
    public String getFullTmpName(String fileName ) {
		
    	return mibLocation.getAbsolutePath() + File.separator + TmpFilePrefix + File.separator +  fileName;
	}
	
	
	/**
	 * Creates temporary directories to hold modules in the repository during loading.
	 * If a MIB module fails loading, the corresponding module in the tmp folding will be rollback. 
	 *
	 * @throws IntegerException the integer exception
	 */
	private void createTmpDirs() throws IntegerException {
		
		File dir = new File( mibLocation.getAbsolutePath() + File.separator  + TmpFilePrefix );
		if ( dir.exists() ) {			
			for ( File f : dir.listFiles() ) {
				f.delete();
			}
		}
		else if ( !dir.mkdirs() )  {
			
			// TODO: this message needs to be moved to the locale bundle.
			throw new IntegerException(null, CommonErrorCodes.IOError,
					new DisplayableInterface[] { new NonLocaleErrorMessage("Can not create a tmp directory for processing")}); 
		}
	}
	
	
	/**
	 * This Class keeps Module information during loading.
	 *
	 * @author dchan
	 */
	public class MibProcessingInfo {
		
		/** The file name on the module */
		String fileName;
		
		/** If isCommon is true. It is considering a common MIB */
		boolean isCommon;
		
		/** Used to indicating the module being succesfully loaded. */
		boolean importSuccess;
		
		/** The import MIB information passing by the client. */
		MIBImportInfo importInfo;
		
		/**
		 * Instantiates a new mib processing info.
		 *
		 * @param mInfo 
		 */
		public MibProcessingInfo( MIBImportInfo mInfo ) {
		
			this.importInfo = mInfo;
		}
	}
	
	
	/**
	 * Get full MIB name.  The MIB full name should be same as the MIB definitions name.
	 * 
	 * @param fileName
	 * @param isCommon
	 * @param content
	 * @return
	 */
	public String getFullMIBName( String fileName,  String content )
	{
		String[] stringArray = content.split("\\s+");
		
		String mib = null;
		for ( int i=0; i<stringArray.length; i++ ) {
			
			if ( stringArray[i].equalsIgnoreCase("DEFINITIONS") ) {
				
				if ( i > 0 && (i+3) < stringArray.length   ) 
				{
					if ( stringArray[i+1].equalsIgnoreCase("::=" ) && stringArray[i+2].equalsIgnoreCase("BEGIN")) {
						mib = stringArray[i-1];
					}
					else if ( stringArray[i+1].equalsIgnoreCase("::=BEGIN" )) {
						mib = stringArray[i-1];
					}
					
				}
			}
			else if ( stringArray[i].equalsIgnoreCase("DEFINITIONS::=")) {
				if ( stringArray[i+1].equalsIgnoreCase("BEGIN" )) {
					mib = stringArray[i-1];
				}
			}
		}
		if ( mib != null ) {
			
			return mibLocation.getAbsolutePath() + File.separator + mib;			
		}
		return null;
	}

}
