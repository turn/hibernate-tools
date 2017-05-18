/*
 * Created on 2004-11-24
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import java.util.EnumSet;
import java.util.Iterator;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JdbcUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author max
 * @author koen
 *
 */
public class ForeignKeysTest {

	static final String[] CREATE_SQL = new String[] {
				"CREATE TABLE MASTER (ID CHAR NOT NULL, NAME VARCHAR(20), PRIMARY KEY (ID))",					
				"CREATE TABLE CHILD  (CHILDID CHARACTER NOT NULL, MASTERREF CHARACTER, PRIMARY KEY(CHILDID), FOREIGN KEY (MASTERREF) REFERENCES MASTER(ID))",				
				"CREATE TABLE CONNECTION  (CONID INT, NAME VARCHAR(50), MASTERREF CHAR, CHILDREF1 CHARACTER, CHILDREF2 CHARACTER, PRIMARY KEY(CONID), CONSTRAINT CON2MASTER FOREIGN KEY (MASTERREF) REFERENCES MASTER(ID), CONSTRAINT CHILDREF1 FOREIGN KEY (CHILDREF1) REFERENCES CHILD(CHILDID), CONSTRAINT CHILDREF2 FOREIGN KEY (CHILDREF2) REFERENCES CHILD(CHILDID))"
				// todo - link where pk is fk to something						
		};

	static final String[] DROP_SQL = new String[] {
				"DROP TABLE CONNECTION",				
				"DROP TABLE CHILD",
				"DROP TABLE MASTER",					
		};
	
	private JDBCMetaDataConfiguration jmdcfg = null;

	@Before
	public void setUp() {
		JdbcUtil.createDatabase(this);;
		jmdcfg = new JDBCMetaDataConfiguration();
		jmdcfg.readFromJDBC();
	}

	@After
	public void tearDown() {
		JdbcUtil.dropDatabase(this);;
	}	
	
	@Test
	public void testMultiRefs() {		
		Table table = jmdcfg.getTable(JdbcUtil.toIdentifier(this, "CONNECTION") );		
		ForeignKey foreignKey = HibernateUtil.getForeignKey(
				table, 
				JdbcUtil.toIdentifier(this, "CON2MASTER") );	
		Assert.assertNotNull(foreignKey);			
		Assert.assertEquals(
				jmdcfg.getReverseEngineeringStrategy().tableToClassName(
						new TableIdentifier(null, null, "MASTER")),
				foreignKey.getReferencedEntityName() );
        Assert.assertEquals(
        		JdbcUtil.toIdentifier(this, "CONNECTION"), 
        		foreignKey.getTable().getName() );	
		Assert.assertEquals(
				jmdcfg.getTable(JdbcUtil.toIdentifier(this, "MASTER") ), 
				foreignKey.getReferencedTable() );
		Assert.assertNotNull(
				HibernateUtil.getForeignKey(
						table, 
						JdbcUtil.toIdentifier(this, "CHILDREF1") ) );
		Assert.assertNotNull(
				HibernateUtil.getForeignKey(
						table, 
						JdbcUtil.toIdentifier(this, "CHILDREF2") ) );
		Assert.assertNull(
				HibernateUtil.getForeignKey(
						table, 
						JdbcUtil.toIdentifier(this, "DUMMY") ) );
		JUnitUtil.assertIteratorContainsExactly(null, table.getForeignKeyIterator(), 3);
	}
	
	@Test
	public void testMasterChild() {		
		Assert.assertNotNull(jmdcfg.getTable(JdbcUtil.toIdentifier(this, "MASTER")));
		Table child = jmdcfg.getTable(JdbcUtil.toIdentifier(this, "CHILD") );	
		Iterator<?> iterator = child.getForeignKeyIterator();		
		ForeignKey fk = (ForeignKey) iterator.next();		
		Assert.assertFalse("should only be one fk", iterator.hasNext() );	
		Assert.assertEquals(1, fk.getColumnSpan() );
		Assert.assertSame(
				fk.getColumn(0), 
				child.getColumn(
						new Column(JdbcUtil.toIdentifier(this, "MASTERREF"))));		
	}
	
	@Test
	public void testExport() {
		SchemaExport schemaExport = new SchemaExport();
		Metadata metadata = jmdcfg.getMetadata();
		final EnumSet<TargetType> targetTypes = EnumSet.noneOf( TargetType.class );
		targetTypes.add( TargetType.STDOUT );
		schemaExport.create(targetTypes, metadata);		
	}
	
}
