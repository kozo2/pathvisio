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
package org.pathvisio.model;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

/**
 * @author Rebecca Tang
 */
public class TestPropertyManager extends TestCase {

      public void testProcessPropertiesXML(){

          InputStream in = null;
          try{
              in = PropertyManager.class.getResourceAsStream("properties.xml");
              PropertyManager.processPropertiesXML(in);

              //modes
              System.out.println("---------- modes ----------------");
              Property prop = PropertyManager.getProperty("biopax");
              assertNotNull(prop);
              printMode(prop);

              prop = PropertyManager.getProperty("mim");
              assertNotNull(prop);
              printMode(prop);

              prop = PropertyManager.getProperty("pharmgkb");
              assertNotNull(prop);
              printMode(prop);

              prop = PropertyManager.getProperty("adv");
              assertNotNull(prop);
              printMode(prop);

              // prperties
              System.out.println("----------properties ----------------");
              prop = PropertyManager.getProperty("nodeType");
              assertNotNull(prop);
              printProp (prop);


              prop = PropertyManager.getProperty("cellularLocation");
              assertNotNull(prop);
              printProp (prop);

              prop = PropertyManager.getProperty("modification");
              assertNotNull(prop);
              printProp (prop);

              prop = PropertyManager.getProperty("role");
              assertNotNull(prop);
              printProp (prop);

              prop = PropertyManager.getProperty("state");
              assertNotNull(prop);
              printProp (prop);

              prop = PropertyManager.getProperty("phenotype");
              assertNotNull(prop);
              printProp (prop);

              prop = PropertyManager.getProperty("accessionId");
              assertNotNull(prop);
              printProp (prop);

              prop = PropertyManager.getProperty("classification");
              assertNotNull(prop);
              printProp (prop);

              prop = PropertyManager.getProperty("coe");
              assertNotNull(prop);
              printProp (prop);

              prop = PropertyManager.getProperty("descriptionUrl");
              assertNotNull(prop);
              printProp (prop);

              prop = PropertyManager.getProperty("diseases");
              assertNotNull(prop);
              printProp (prop);

              prop = PropertyManager.getProperty("path");
              assertNotNull(prop);
              printProp (prop);

              prop = PropertyManager.getProperty("relatedGenes");
              assertNotNull(prop);
              printProp (prop);

              prop = PropertyManager.getProperty("relatedDrugs");
              assertNotNull(prop);
              printProp (prop);

              prop = PropertyManager.getProperty("relatedDiseases");
              assertNotNull(prop);
              printProp (prop);


              //object properties
              System.out.println("----------objects ----------------");
              ObjectProperties objProp = PropertyManager.getObjectProperties(ObjectType.MAPPINFO);
              assertNotNull(objProp);
              printObjProps(objProp);

              objProp = PropertyManager.getObjectProperties(ObjectType.STATE);
              assertNotNull(objProp);
              printObjProps(objProp);

              objProp = PropertyManager.getObjectProperties(ObjectType.DATANODE);
              assertNotNull(objProp);
              printObjProps(objProp);

          } catch (Exception e) {
              e.printStackTrace();
              fail();
          } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }
      }




      private void printMode(Property mode){
          System.out.print("---");
          System.out.println( "mode " + mode.getId() + " " + mode.getName() + " " + mode.getType());
      }

      private void printProp(Property prop){
          System.out.print("--------");
          System.out.println(prop.getId() + " " + prop.getName() + " " + prop.getType());
          if (prop.getModes() != null){
              System.out.println("prop modes:");
              Set<Property> modes = prop.getModes();
              for (Property mode : modes){
                  printMode(mode);
              }
          }
          if (prop.getValues() != null){
              List<PropertyEnum> options = prop.getValues();
              for (PropertyEnum op : options){
                  System.out.println("op " + op.getValue());
                  if (op.getModes() != null){
                      Set<Property> modes = op.getModes();
                      System.out.println("op modes:");
                      for (Property mode : modes){
                          printMode(mode);
                      }
                  }
              }
          }
      }

      private void printObjProps(ObjectProperties objProp){
          System.out.println("=================================");
          System.out.println("obj: " + objProp.getId() + " " + objProp.getName() + objProp.getType());
          List<Property> prop = objProp.getProperties();
          System.out.println("properties");
          for (Property p : prop){
              System.out.println(" " + p.getId());
          }
          List<PropertyType> staticProp = objProp.getStaticProperties();
          System.out.println("static properties");
          for (PropertyType pt : staticProp){
              System.out.println(" " + pt.name());
          }
          if (objProp.getId().equals("dataNode")){
              List<Property> subProps = objProp.getSubProperties(PropertyManager.getProperty("nodeType"), "Protein");
              for (Property sp : subProps){
                  System.out.println("subprop for protein " + sp.getName());
              }
          }
      }
}
