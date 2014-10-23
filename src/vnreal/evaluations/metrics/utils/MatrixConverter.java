package vnreal.evaluations.metrics.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import vnreal.demands.AbstractDemand;
import vnreal.mapping.Mapping;
import vnreal.network.NetworkStack;
import vnreal.network.Node;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

/**
 * This class is to generate different matrices based on the network stack 
 * where the virtual networks and the substrate network is situated.
 * 
 * @author baskefab
 *
 */
public class MatrixConverter {
    
    
    //utility class
    private MatrixConverter() {
        
    }
    
    /**
     * This method is to generate an adjacency matrix out of the stack.
     * @param stack the stack to be transformed into an adjacency matrix.
     * @return the adjacency matrix.
     */
    public static int[][] generateAdjacencyMatrix(NetworkStack stack) {
        int count;
        if (!stack.getVirtualNetworks().isEmpty()) {
            count = 0;
        } else {
            count = 0;
        }

        for (Iterator<VirtualNetwork> vns = stack.getVirtualNetworks().iterator(); vns.hasNext();) {
            
            VirtualNetwork vn = vns.next();
            
            for (Iterator<VirtualNode> vnode = vn.getVertices().iterator(); vnode.hasNext();) {
                vnode.next();
                count += 1;
            }
        }

        int size = stack.getSubstrate().getSize();
        int totalSize = size + count;
        
        int[][] matrix = new int[totalSize][totalSize];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                Node  node1;
                Node node2;
                Collection<SubstrateLink> a = null;
                Collection<SubstrateLink> slinki = null;
                Collection<VirtualLink> b = null;
                Collection<VirtualLink> vlinki = null;
                
                if (i < size) {
                    //stream substrate
                    //node1 = new SubstrateNode();
                    for (SubstrateNode snode : stack.getSubstrate().getVertices()) {
                        if (snode.getId() == i) {
                            node1 = snode;
                            slinki = stack.getSubstrate().getIncidentEdges(snode);
                            break;
                        }
                    }
                } else {
                    //stream virtual networks               
                    //node1 = new VirtualNode();
                    boolean runner = true;
                    int counter = i - size;
                    for (VirtualNetwork vns : stack.getVirtualNetworks()) {
                        //stream virtual nodes
                        for (VirtualNode vnn : vns.getVertices()) {
                            if (counter == 0 && runner) {
                                node1 = vnn;
                                vlinki = vns.getIncidentEdges(vnn);
                                runner = false;
                                break;
                            } else {
                                counter--;
                            }
                        }
                    }
                }
                
                if (j < size) {
                    //stream substrate
                    //node2 = new SubstrateNode();                    
                    for (SubstrateNode snode2 : stack.getSubstrate().getVertices()) {
                        if (snode2.getId() == j) {
                            node2 = snode2;
                            a = stack.getSubstrate().getIncidentEdges(snode2);
                            break;
                        }
                    }
                } else {
                    //stream virtual networks
                    //node2 = new VirtualNode();
                    boolean runner = true;
                    int counter = j - size;
                    for (VirtualNetwork vns : stack.getVirtualNetworks()) {
                        //stream virtual nodes
                        for (VirtualNode vnn : vns.getVertices()) {
                            if (counter == 0 && runner) {
                                node2 = vnn;
                                b = vns.getIncidentEdges((VirtualNode) node2);
                                runner = false;
                                break;
                            } else {
                                counter--;
                            }
                        }
                    }
                }
                if (a != null && !a.isEmpty()) {
                    if (vlinki != null && !vlinki.isEmpty()) {
                        for (SubstrateLink link : a) {
                            for (VirtualLink llink : vlinki) {
                                if (link.equals(llink) && i != j) {
                                    matrix[i][j] = 1;
                                }
                            }
                        }
                    
                    } else {
                        for (SubstrateLink link : a) {
                            for (SubstrateLink llink : slinki) {
                                if (link.equals(llink) && i != j) {
                                    matrix[i][j] = 1;
                                }
                            }
                        }
                    }
                } else if (b != null && !b.isEmpty()) {
                    if (vlinki != null && !vlinki.isEmpty()) {
                        for (VirtualLink link : b) {
                            for (VirtualLink llink : vlinki) {
                                if (link.equals(llink) && i != j) {
                                    matrix[i][j] = 1;
                                }
                            }
                        }
                    
                    } else {
                        for (VirtualLink link : b) {
                            for (SubstrateLink llink : slinki) {
                                if (link.equals(llink) && i != j) {
                                    matrix[i][j] = 1;
                                }
                            }
                        }
                    }
                }
            }
        }
        return matrix;
        
    }
    
    /**
     * This method is to generate an incidence matrix out of the stack.
     * @param stack the stack to be transformed into an incidence matrix.
     * @return the incidence matrix.
     */
    public static int[][] generateIncidenceMatrix(NetworkStack stack) {
        int count;
        if (!stack.getVirtualNetworks().isEmpty()) {
            count = 0;
        } else {
            count = 0;
        }

        for (Iterator<VirtualNetwork> vns = stack.getVirtualNetworks().iterator(); vns.hasNext();) {
            
            VirtualNetwork vn = vns.next();
            
            for (Iterator<VirtualNode> vnode = vn.getVertices().iterator(); vnode.hasNext();) {
                vnode.next();
                count += 1;
            }
            /*
            for (VirtualNode vrnode : vn.getVertices()) {
                for (AbstractDemand ad : vrnode.get()) {
                    for (Mapping map : ad.getMappings()) {
                        if (map.getResource().getOwner() instanceof SubstrateNode) {
                            long snodeid = map.getResource().getOwner().getId();
                            for (SubstrateNode snn : stack.getSubstrate().getVertices()) {
                                if (snn.getId() == snodeid) {
                                    
                                }
                            }
                        }
                    }
                }
            }*/
        }

        int size = stack.getSubstrate().getSize();
        int totalSize = size + count;
        
        int[][] matrix = new int[totalSize][totalSize];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                Node  node1 = null;
                Node node2 = null;
                Collection<SubstrateLink> a = null;
                Collection<SubstrateLink> slinki = null;
                Collection<VirtualLink> b = null;
                Collection<VirtualLink> vlinki = null;
                VirtualNetwork vn1 = null;
                VirtualNetwork vn2 = null;
                
                
                if (i < size && j < size) {
                    //stream substrate
                    //node1 = new SubstrateNode();
                    for (SubstrateNode snode : stack.getSubstrate().getVertices()) {
                        if (snode.getId() == i) {
                            node1 = snode;
                          //stack.getSubstrate().
                            Collection<SubstrateNode> coll = stack.getSubstrate().getSuccessors((SubstrateNode) node1);
                            for (SubstrateNode snode2 : coll) {
                                if (snode2.getId() == j) {
                                    matrix[i][j] = 1;
                                }
                            }
                            //slinki = stack.getSubstrate().getIncidentEdges(snode);
                            //break;
                        }
                    }
                } else if (i >= size && j >= size) {
                    //stream virtual networks               
                    //node1 = new VirtualNode();
                    boolean runner = true;
                    int counter = i - size;
                    for (VirtualNetwork vns : stack.getVirtualNetworks()) {
                        for (VirtualNode vnn : vns.getVertices()) {
                            if (counter == 0 && runner) {
                                node1 = vnn;
                                long pos = vnn.getId();
                                int sizevn = vns.getSize();
                                Collection<VirtualNode> coll = vns.getSuccessors(vnn);
                                for (VirtualNode vnode : coll) {
                                    long pos2 = vnode.getId();
                                    if (pos2 > pos) {
                                        int diff = (int) (pos2 - pos);
                                        matrix[i][i + diff] = 1;
                                    } else if (pos > pos2) {
                                        int diff = (int) (pos - pos2);
                                        matrix[i][i - diff] = 1;
                                    }
                                }
                                runner = false;
                            } else {
                                counter--;
                            }
                        }
                    }
                    
                    /*
                    boolean runner = true;
                    boolean runner2 = true;
                    int counter = i - size;
                    
                    for (VirtualNetwork vns : stack.getVirtualNetworks()) {  
                        int counterj = j - size;
                        //stream virtual nodes
                        for (VirtualNode vnn : vns.getVertices()) {
                            if (counter == 0 && runner) {
                                node1 = vnn;
                                Collection<VirtualNode> coll = vns.getSuccessors(vnn);
                                //null?
                                for (VirtualNode vnode : coll) {
                                    if (counterj == 0 && runner2) {
                                        matrix[i][j] = 1;
                                        runner2 = false;
                                    } else {
                                        counterj--;
                                    }
                                }
                                //vlinki = vns.getIncidentEdges(vnn);
                                runner = false;
                                //break;
                            } else {
                                counter--;
                                
                            }
                        }
                    }*/                    
                    
                } else {
                    matrix[i][j] = 0;
                }               
                
                        
            }
            
        }
        return matrix;
    }
    
    
    /**
     * This method is to generate a matrix representing the modified virtual 
     * network considering all hidden hops as stated in the Bachelor Thesis.
     * @param stack the stack to be transformed into the matrix representing
     * the modified virtual network.
     * @return the matrix.
     */
    public static int[][] generateModifiedVirtualNetwork(NetworkStack stack) {
        
        VirtualNetwork vnetwork = stack.getVirtualNetworks().get(0);
        Collection<SubstrateLink> coll = new ArrayList<SubstrateLink>();
        Collection<VirtualNode[]> vColl = new ArrayList<VirtualNode[]>();
        //int numberOfSubNodes = stack.getSubstrate().getSize();
        //int numberOfSubLinks = stack.getSubstrate().getEdgeCount();
        //int numberOfSubElem = numberOfSubNodes + numberOfSubLinks;
        int numberOfSubElem = (int) stack.getVirtualNetworks().get(0).getVertices().iterator().next().getId();
        /*
        for (VirtualNode vnode : stack.getVirtualNetworks().get(0).getVertices()) {
            if (vnode.getId() < var) {
                var = vnode.getId();
            }
        }*/
        
        int size = vnetwork.getSize();
        for (VirtualLink vl : vnetwork.getEdges()) {  
            VirtualNode[] vNodes = new VirtualNode[2];
            int counter2 = 0;
            for (VirtualNode vnn : vnetwork.getIncidentVertices(vl)) {
                vNodes[counter2] = vnn;
                counter2++;
            }
            boolean goOn = true;
            
            if (vColl.isEmpty()) {
                vColl.add(vNodes);
            } else {
                for (VirtualNode[] iter : vColl) {
                    if ((iter[0] == vNodes[0] || iter[0] == vNodes[1]) && (iter[1] == vNodes[0] || iter[1] == vNodes[1])) {
                        goOn = false;
                        break;
                    } 
                }
            }
            if (goOn) {
                vColl.add(vNodes);
                for (AbstractDemand ad : vl.get()) {
                    if (ad.getMappings().size() > 1) {
                        int sizer = 0;
                        for (Mapping map : ad.getMappings()) {
                            if (map.getResource().getOwner() instanceof SubstrateLink) {
                                SubstrateLink link = (SubstrateLink) map.getResource().getOwner();
                                if (coll.isEmpty()) {
                                    sizer++;
                                    coll.add(link);
                                } else {
                                    boolean contained = false;
                                    for (SubstrateLink slink : coll) {
                                        if (slink.getId() == link.getId()) {
                                            contained = true;
                                            break;
                                        } 
                                    }
                                    if (!contained) {
                                        sizer++;
                                        coll.add(link);
                                    }
                                }
                                
                            }
                        } if (sizer > 1) {
                            size += sizer - 1;
                        }                                    
                    }
                    
                }    
            }
                    
        }
        vColl.clear();
        int[][] matrix = new int[size][size];
        int minMeta = vnetwork.getSize();
        int minMeta2 = vnetwork.getSize() + 1;
        for (VirtualLink vl : vnetwork.getEdges()) {
            
            
            VirtualNode[] vNodes = new VirtualNode[2];
            int counter2 = 0;
            for (VirtualNode vnn : vnetwork.getIncidentVertices(vl)) {
                vNodes[counter2] = vnn;
                counter2++;
            }
            boolean goOn = true;
            
            if (vColl.isEmpty()) {
                vColl.add(vNodes);
            } else {
                for (VirtualNode[] iter : vColl) {
                    if ((iter[0] == vNodes[0] || iter[0] == vNodes[1]) && (iter[1] == vNodes[0] || iter[1] == vNodes[1])) {
                        goOn = false;
                        break;
                    } 
                }
            }
            
            if (goOn) {
                vColl.add(vNodes);
                int amountOfMaps = 0;
                for (AbstractDemand ad : vl.get()) {
                    if (ad.getMappings().size() > amountOfMaps) {
                        amountOfMaps = ad.getMappings().size();
                    }
                    //amountOfMaps += ad.getMappings().size();
                }
                if (amountOfMaps > 1) {
                    if (amountOfMaps == 2) {
                        matrix[(int) (vNodes[0].getId() - numberOfSubElem)][minMeta] = 1;
                        matrix[minMeta][(int) (vNodes[0].getId()  -numberOfSubElem)] = 1;
                        matrix[minMeta][(int) (vNodes[1].getId() - numberOfSubElem)] = 1;
                        matrix[(int) (vNodes[1].getId() - numberOfSubElem)][minMeta] = 1;
                        minMeta++;
                        
                        minMeta2++;
                       
                    } else {
                        matrix[(int) (vNodes[0].getId() - numberOfSubElem)][minMeta] = 1;
                        matrix[minMeta][(int) (vNodes[0].getId()  -numberOfSubElem)] = 1;
                        while (amountOfMaps - 1 > 1) {
                            matrix[minMeta][minMeta2] = 1;
                            matrix[minMeta2][minMeta] = 1;
                            if (amountOfMaps - 1 > 2) {
                                minMeta++;
                                minMeta2++;
                            }
                            
                            amountOfMaps--;
                        }
                        matrix[minMeta2][(int) (vNodes[1].getId() - numberOfSubElem)] = 1;
                        matrix[(int) (vNodes[1].getId() - numberOfSubElem)][minMeta2] = 1;
                        minMeta++;
                        minMeta++;
                        minMeta2++;
                        minMeta2++;
                    }
                    
                } else {
                    matrix[(int) (vNodes[0].getId()  - numberOfSubElem)][(int) (vNodes[1].getId() - numberOfSubElem)] = 1;
                    matrix[(int) (vNodes[1].getId() - numberOfSubElem)][(int) (vNodes[0].getId() - numberOfSubElem)] = 1;
                    
                }
            }
            
        }
        return matrix;
    }
    
   /**
    * This method is to generate a matrix out of the stack representing the 
    * virtual network before any modification.
    * @param stack the stack to be transformed.
    * @return the matrix.
    */
    public static int[][] generateVirtualMatrix(NetworkStack stack) {
        VirtualNetwork vnetwork = stack.getVirtualNetworks().get(0);
        Collection<VirtualNode[]> vColl = new ArrayList<VirtualNode[]>();
        int size = vnetwork.getSize();
        int[][] matrix = new int[size][size];
        //int numberOfSubNodes = stack.getSubstrate().getSize();
        //int numberOfSubLinks = stack.getSubstrate().getEdgeCount();
        int numberOfSubElem = (int) stack.getVirtualNetworks().get(0).getVertices().iterator().next().getId();
        
        for (VirtualLink vlink : vnetwork.getEdges()) {
            VirtualNode[] vNodes = new VirtualNode[2];
            int counter2 = 0;
            for (VirtualNode vnn : vnetwork.getIncidentVertices(vlink)) {
                vNodes[counter2] = vnn;
                counter2++;
            }
            
            boolean goOn = true;
            
            if (vColl.isEmpty()) {
                vColl.add(vNodes);
            } else {
                for (VirtualNode[] iter : vColl) {
                    if ((iter[0] == vNodes[0] || iter[0] == vNodes[1]) && (iter[1] == vNodes[0] || iter[1] == vNodes[1])) {
                        goOn = false;
                        break;
                    } 
                }
            }
            
            if (goOn) {
                vColl.add(vNodes);
                matrix[(int) (vNodes[0].getId()  - numberOfSubElem)][(int) (vNodes[1].getId() - numberOfSubElem)] = 1;
                matrix[(int) (vNodes[1].getId() - numberOfSubElem)][(int) (vNodes[0].getId() - numberOfSubElem)] = 1;
            }
            
        }
        return matrix;
    }
    
    /**
     * This method is to generate a matrix out of the stack which represents the 
     * augmented graph of the virtualized network as stated in the Bachelor Thesis.
     * 
     * @param stack the stack to be transformed into a matrix representing the 
     * augmented graph.
     * @return the matrix.
     */
    public static int[][] generateAugmentedGraph2(NetworkStack stack) {
        int countVirtualNodes = 0;
        int countVirtualLinks = 0;
        int countSubstrateLinks = 0;
        Collection<SubstrateLink> coll = null;
        Collection<SubstrateLink> copyColl = new ArrayList<SubstrateLink>();
        for (SubstrateLink ssl : stack.getSubstrate().getEdges()) {
            copyColl.add(ssl);
        }
        Collection<VirtualLink> copy = new ArrayList<VirtualLink>();
        for (VirtualNetwork vnet : stack.getVirtualNetworks()) {
            for (VirtualLink vlinki : vnet.getEdges()) {
                copy.add(vlinki);
            }
        }
        for (Iterator<VirtualNetwork> vns = stack.getVirtualNetworks().iterator(); vns.hasNext();) {
            VirtualNetwork vn = vns.next();
            for (Iterator<VirtualNode> vnode = vn.getVertices().iterator(); vnode.hasNext();) {
                vnode.next();
                countVirtualNodes += 1;
            }
            for (VirtualLink vlink : vn.getEdges()) {
                for (AbstractDemand ad : vlink.get()) {
                    for (Mapping map : ad.getMappings()) {
                        if (map.getResource().getOwner() instanceof SubstrateLink) {
                            if (coll != null) {
                                boolean duplicate = false;
                                for (SubstrateLink iter : coll) {
                                    if (iter.getId() == map.getResource().getOwner().getId()) {
                                        duplicate = true;
                                        break;
                                    }
                                }
                                if (!duplicate) {
                                    coll.add((SubstrateLink) map.getResource().getOwner());
                                    copyColl.remove((SubstrateLink) map.getResource().getOwner());
                                }
                            } else {
                                coll = new ArrayList<SubstrateLink>();
                                coll.add((SubstrateLink) map.getResource().getOwner());
                                copyColl.remove(map.getResource().getOwner());
                            }
                        }
                    }
                }
            }
            for (Iterator<VirtualLink> vlink = vn.getEdges().iterator(); vlink.hasNext();) {
                vlink.next();
                countVirtualLinks += 1;
            }
        }
        for (Iterator<SubstrateLink> slink = coll.iterator(); slink.hasNext();) {
            slink.next();
            countSubstrateLinks += 1;
        }
        
        int numberOfSubstrateNodes = stack.getSubstrate().getSize();
        int totalNumberOfVertices = numberOfSubstrateNodes + countVirtualNodes + countVirtualLinks + countSubstrateLinks;
        
        int[][] matrix = new int[totalNumberOfVertices][totalNumberOfVertices];
        int minVirtualNode = numberOfSubstrateNodes;
        int maxVirtualNode = countVirtualNodes + numberOfSubstrateNodes - 1;
        int minMetaSubstrate = maxVirtualNode + 1;
        int maxMetaSubstrate = maxVirtualNode + coll.size();
        int minMetaVirtual = maxMetaSubstrate + 1;

        int mod = minMetaSubstrate;
        int mod2 = minMetaVirtual;
        
        for (SubstrateLink linki : copyColl) {
            int m = 0;
            SubstrateNode[] orig = new SubstrateNode[2];
            for (SubstrateNode sn : stack.getSubstrate().getIncidentVertices(linki)) {
                orig[m] = sn;
                m++;
            }
            matrix[(int) orig[0].getId()][(int) orig[1].getId()] = 1;
            matrix[(int) orig[1].getId()][(int) orig[0].getId()] = 1;
        }
        
        for (VirtualNetwork vnetw : stack.getVirtualNetworks()) {
            for (VirtualNode vnodi : vnetw.getVertices()) {
                int equalizer = 0;
                for (VirtualNetwork vnet : stack.getVirtualNetworks()) {
                    if (vnet.getLayer() == vnodi.getLayer()) {
                        break;
                    } else {
                        for (VirtualLink vll : vnet.getEdges()) {
                            equalizer++;
                        }
                    }
                }
                for (AbstractDemand ad : vnodi.get()) {
                    for (Mapping map : ad.getMappings()) {
                        matrix[(int) map.getResource().getOwner().getId()][(int) (vnodi.getId() - equalizer - stack.getSubstrate().getEdgeCount())] = 1;
                        matrix[(int) (vnodi.getId() - equalizer - stack.getSubstrate().getEdgeCount())][(int) map.getResource().getOwner().getId()] = 1;
                    }
                }
            }
        }

        for (VirtualLink vlinki : copy) {
            for (AbstractDemand ad : vlinki.get()) {
                for (Mapping map : ad.getMappings()) {
                    if (map.getResource().getOwner() instanceof SubstrateLink) {
                        SubstrateLink search = (SubstrateLink) map.getResource().getOwner();
                        SubstrateNode[] sNodes = new SubstrateNode[2];
                        int counter = 0;
                        for (SubstrateNode sn : stack.getSubstrate().getIncidentVertices(search)) {
                            sNodes[counter] = sn;
                            counter++;
                        }
                        VirtualNode[] vNodes = new VirtualNode[2];
                        int counter2 = 0;
                        for (VirtualNode vnn : stack.getVirtualNetworks().get(vlinki.getLayer() - 1).getIncidentVertices(vlinki)) {
                            vNodes[counter2] = vnn;
                            counter2++;
                        }
                        int equalizer = 0;
                        for (VirtualNetwork vnet : stack.getVirtualNetworks()) {
                            if (vnet.getLayer() == vlinki.getLayer()) {
                                break;
                            } else {
                                for (VirtualLink vll : vnet.getEdges()) {
                                    equalizer++;
                                }
                            }
                        }
                        boolean contained = false;
                        for (SubstrateLink slinki : coll) {
                            if (slinki.getId() == search.getId()) {
                                coll.remove(slinki);
                                matrix[(int) sNodes[0].getId()][mod] = 1;
                                matrix[mod][(int) sNodes[0].getId()] = 1;
                                matrix[mod][(int) sNodes[1].getId()] = 1;
                                matrix[(int) sNodes[1].getId()][mod] = 1;
                                matrix[(int) (vNodes[0].getId() - stack.getSubstrate().getEdgeCount() - equalizer)][mod2] = 1;
                                matrix[mod2][(int) (vNodes[0].getId() - stack.getSubstrate().getEdgeCount() - equalizer)] = 1;
                                matrix[(int) (vNodes[1].getId() - stack.getSubstrate().getEdgeCount() - equalizer)][mod2] = 1;
                                matrix[mod2][(int) (vNodes[1].getId() - stack.getSubstrate().getEdgeCount() - equalizer)] = 1;
                                matrix[mod2][mod] = 1;
                                matrix[mod][mod2] = 1;
                                contained = true;
                                //mod++;
                                break;
                            }
                        }
                        if (!contained) {
                            int required = 0;
                            for (int k = minMetaSubstrate; k < maxMetaSubstrate; k++) {
                                if (matrix[(int) sNodes[0].getId()][k] == 1
                                        && matrix[(int) sNodes[1].getId()][k] == 1) {
                                    required = k;
                                    break;
                                }
                            }
                            
                            matrix[(int) (vNodes[0].getId() - stack.getSubstrate().getEdgeCount() - equalizer)][mod2] = 1;
                            matrix[mod2][(int) (vNodes[0].getId() - stack.getSubstrate().getEdgeCount() - equalizer)] = 1;
                            matrix[(int) (vNodes[1].getId() - stack.getSubstrate().getEdgeCount() - equalizer)][mod2] = 1;
                            matrix[mod2][(int) (vNodes[1].getId() - stack.getSubstrate().getEdgeCount() - equalizer)] = 1;
                            matrix[mod2][required] = 1;
                            matrix[required][mod2] = 1;
                            //mod2++;
                        }
                    }
                    mod++;
                }
            }
            mod2++;
        }
                
                /*
                int counter = minMetaSubstrate;
                
                if (i < numberOfSubstrateNodes) {
                    if (j < numberOfSubstrateNodes && i != j) {
                        SubstrateNode snode1 = null;
                        SubstrateNode snode2 = null;
                        for (SubstrateNode sn : stack.getSubstrate().getVertices()) {
                            if (sn.getId() == i) {
                                snode1 = sn;
                            } else if (sn.getId() == j) {
                                snode2 = sn;
                            }
                        }
                        SubstrateLink searchLink = null;
                        boolean isLink = false;
                        Collection<SubstrateLink> coll1 = stack.getSubstrate().getIncidentEdges(snode1);
                        Collection<SubstrateLink> coll2  = stack.getSubstrate().getIncidentEdges(snode2);
                        for (SubstrateLink slink : coll1) {
                            for (SubstrateLink sllink : coll2) {
                                if (slink.getId() == sllink.getId()) {
                                    searchLink = sllink;
                                    isLink = true;
                                    break;
                                }
                            }
                        }
                        if (isLink) {
                            boolean meta = false;
                            for (SubstrateLink sll : coll) {
                                if (sll.getId() == searchLink.getId()) {
                                    meta = true;
                                }
                            }
                            if (!meta) {
                                matrix[i][j] = 1;
                            } else {
                                matrix[i][counter] = 1;
                                matrix[counter][j] = 1;
                                matrix[counter][i] = 1;
                                matrix[j][counter] = 1;
                                counter++;
                                coll.remove(searchLink);
                            }
                        }
                                                
                    }
                } else if (i < maxVirtualNode) {
                    if (j < maxVirtualNode && i != j) {
                        int modifier = stack.getSubstrate().getEdgeCount();
                        VirtualNode vnode1 = null;
                        VirtualNode vnode2 = null;
                        int layer = 0;
                        for (VirtualNetwork vn : stack.getVirtualNetworks()) {
                            for (VirtualNode vnode : vn.getVertices()) {
                                if (vnode.getId() - countSubstrateLinks == i) {
                                    vnode1 = vnode;
                                } else if (vnode.getId() - countSubstrateLinks == j) {
                                    vnode2 = vnode;
                                }                                
                            }
                            if (vnode1 != null && vnode2 != null) {
                                layer = vn.getLayer();
                                break;
                            }
                        }
                        assert vnode1 != null && vnode2 != null;
                        VirtualLink searchLink = null;
                        boolean isLink = false;
                        Collection<VirtualLink> coll1 = stack.getVirtualNetworks().get(layer).getIncidentEdges(vnode1);
                        Collection<VirtualLink> coll2  = stack.getVirtualNetworks().get(layer).getIncidentEdges(vnode1);
                        for (VirtualLink vlink1 : coll1) {
                            for (VirtualLink vlink2 : coll2) {
                                if (vlink1.getId() == vlink2.getId()) {
                                    searchLink = vlink1;
                                    isLink = true;
                                    break;
                                }
                            }
                        }
                        if (isLink) {
                            matrix[i][counter] = 1;
                            matrix[counter][j] = 1;
                            counter++;
                            copy.remove(searchLink);
                            //stack.getVirtualNetworks().get(layer).removeEdge(searchLink);//KOPIE make of collections!!!
                            //begin with virtual links and their mapping so that first of all the meta nodes are coherent.
                            
                        }
                    }
                } else if (i >= minMetaSubstrate) {
                    if (j >= minMetaSubstrate) {
                        
                    }
                }
                      */         
      
        return matrix;
    }
    
    /**
     * This method is to generate a matrix out of the stack which represents the 
     * augmented graph of the virtualized network as stated in the Bachelor Thesis.
     * 
     * @param stack the stack to be transformed into a matrix representing the 
     * augmented graph.
     * @return the matrix.
     */
    public static int[][] generateAugmentedGraph(NetworkStack stack) {
        
        int numberOfVirtualNodes = 0;
        int numberOfVirtualLinks = 0;
        
        Collection<VirtualNode[]> vColl = new ArrayList<VirtualNode[]>();
        Collection<SubstrateNode[]> sColl = new ArrayList<SubstrateNode[]>();
        
        
        for (Iterator<VirtualNetwork> vns = stack.getVirtualNetworks().iterator(); vns.hasNext();) {
            VirtualNetwork vn = vns.next();
            numberOfVirtualNodes += vn.getSize();
            numberOfVirtualLinks += vn.getEdgeCount() / 2;     
        }
        
        
        int numberOfSubstrateNodes = stack.getSubstrate().getSize();
        int numberOfSubstrateLinks = stack.getSubstrate().getEdgeCount() / 2; //bidirectional case
        int totalNumberOfVertices = numberOfSubstrateNodes + numberOfVirtualNodes + numberOfVirtualLinks + numberOfSubstrateLinks;
        
        int[][] matrix = new int[totalNumberOfVertices][totalNumberOfVertices];
        int minVirtualNode = numberOfSubstrateNodes;
        int maxVirtualNode = numberOfVirtualNodes + minVirtualNode - 1;
        int minMetaSubstrate = maxVirtualNode + 1;
        //int maxMetaSubstrate = maxVirtualNode + coll.size() + copyColl.size();
        int maxMetaSubstrate = maxVirtualNode + numberOfSubstrateLinks;
        int minMetaVirtual = maxMetaSubstrate + 1;
        
        int mod = minMetaSubstrate;
        int mod2 = minMetaVirtual;                       
                
        
        int size = 0;
        for (VirtualNetwork vnetw : stack.getVirtualNetworks()) {
            int firstElem = (int) vnetw.getVertices().iterator().next().getId();
            
            for (VirtualNode vnodi : vnetw.getVertices()) {
                /*
                int equalizer = 0;
                for (VirtualNetwork vnet : stack.getVirtualNetworks()) {
                    if (vnet.getLayer() == vnodi.getLayer()) {
                        break;
                    } else {
                        for (VirtualLink vll : vnet.getEdges()) {
                            equalizer++;
                        }
                    }
                }*/
                for (AbstractDemand ad : vnodi.get()) {
                    for (Mapping map : ad.getMappings()) {
                        matrix[(int) map.getResource().getOwner().getId()][(int) (vnodi.getId() + size - firstElem + minVirtualNode)] = 1;
                        matrix[(int) (vnodi.getId() + size - firstElem + minVirtualNode)][(int) map.getResource().getOwner().getId()] = 1;
                    }
                }
            }
            size += vnetw.getSize();
        }

        int size2 = 0;
        for (VirtualNetwork vnetwork : stack.getVirtualNetworks()) {
            int firstElem = (int) vnetwork.getVertices().iterator().next().getId();
            for (VirtualLink vlink : vnetwork.getEdges()) {
                int n = 0;
                VirtualNode[] temp = new VirtualNode[2];
                for (VirtualNode vnode : vnetwork.getIncidentVertices(vlink)) {
                    temp[n] = vnode;
                    n++;
                }
                boolean noDuplicate = true;
                
                if (vColl.isEmpty()) {
                    vColl.add(temp);
                } else {
                    for (VirtualNode[] iter : vColl) {
                        if ((iter[0] == temp[0] || iter[0] == temp[1]) && (iter[1] == temp[0] || iter[1] == temp[1])) {
                            noDuplicate = false;
                            break;
                        }
                    }
                }
                if (noDuplicate) {
                    vColl.add(temp);
                    Collection<SubstrateLink> skipper = new ArrayList<SubstrateLink>();
                    for (AbstractDemand ad : vlink.get()) {
                        for (Mapping map : ad.getMappings()) {
                            boolean skip = true;
                            if (skipper.isEmpty()) {
                                skipper.add((SubstrateLink) map.getResource().getOwner());
                            } else {
                                for (SubstrateLink iter : skipper) {
                                    if (iter.getId() == map.getResource().getOwner().getId()) {
                                        skip = false;
                                        break;
                                    }
                                }
                            }
                            if (skip) {
                                SubstrateLink search = (SubstrateLink) map.getResource().getOwner();
                                skipper.add(search);
                              
                                SubstrateNode[] sNodes = new SubstrateNode[2];
                                int counter = 0;
                                for (SubstrateNode sn : stack.getSubstrate().getIncidentVertices(search)) {
                                    sNodes[counter] = sn;
                                    counter++;
                                }
                                                                
                                boolean unique = true;
                                if (sColl.isEmpty()) {
                                    sColl.add(sNodes);
                                } else {
                                    for (SubstrateNode[] iter : sColl) {
                                        if ((iter[0] == sNodes[0] || iter[0] == sNodes[1]) && (iter[1] == sNodes[0] || iter[1] == sNodes[1])) {
                                            unique = false;
                                            break;
                                        }
                                    }
                                }
                                boolean contained = false;
                                if (unique) {
                                    sColl.add(sNodes);
                                    matrix[(int) sNodes[0].getId()][mod] = 1;
                                    matrix[mod][(int) sNodes[0].getId()] = 1;
                                    matrix[mod][(int) sNodes[1].getId()] = 1;
                                    matrix[(int) sNodes[1].getId()][mod] = 1;
                                    matrix[(int) (temp[0].getId() - firstElem + minVirtualNode + size2)][mod2] = 1;
                                    matrix[mod2][(int) (temp[0].getId() - firstElem + minVirtualNode + size2)] = 1;
                                    matrix[(int) (temp[1].getId() - firstElem + minVirtualNode + size2)][mod2] = 1;
                                    matrix[mod2][(int) (temp[1].getId() - firstElem + minVirtualNode + size2)] = 1;
                                    matrix[mod2][mod] = 1;
                                    matrix[mod][mod2] = 1;
                                    contained = true;
                                }
                                if (!contained) {
                                    int required = 0;
                                    for (int k = minMetaSubstrate; k < maxMetaSubstrate; k++) {
                                        if (matrix[(int) sNodes[0].getId()][k] == 1
                                                && matrix[(int) sNodes[1].getId()][k] == 1) {
                                            required = k;
                                            break;
                                        }
                                    }
                                    
                                    matrix[(int) (temp[0].getId() - firstElem + minVirtualNode + size2)][mod2] = 1;
                                    matrix[mod2][(int) (temp[0].getId() - firstElem + minVirtualNode + size2)] = 1;
                                    matrix[(int) (temp[1].getId() - firstElem + minVirtualNode + size2)][mod2] = 1;
                                    matrix[mod2][(int) (temp[1].getId() - firstElem + minVirtualNode + size2)] = 1;
                                    matrix[mod2][required] = 1;
                                    matrix[required][mod2] = 1;
                                    //mod2++;
                                }
                                mod++;
                            }
                            
                        }
                        
                    }
                    mod2++;
                }
            }
            size2 += vnetwork.getSize();
        }
        
        for (SubstrateLink slink : stack.getSubstrate().getEdges()) {
            int m = 0;
            SubstrateNode[] orig = new SubstrateNode[2];
            for (SubstrateNode sn : stack.getSubstrate().getIncidentVertices(slink)) {
                orig[m] = sn;
                m++;
            }
            boolean goOn = true;
            
            if (sColl.isEmpty()) {
                sColl.add(orig);
            } else {
                for (SubstrateNode[] iter : sColl) {
                    if ((iter[0] == orig[0] || iter[0] == orig[1]) && (iter[1] == orig[0] || iter[1] == orig[1])) {
                        goOn = false;
                        break;
                    } 
                }
            }
            
            if (goOn && mod <= maxMetaSubstrate) {
                sColl.add(orig);
                matrix[(int) orig[0].getId()][mod] = 1;
                matrix[mod][(int) orig[0].getId()] = 1;
                matrix[(int) orig[1].getId()][mod] = 1;
                matrix[mod][(int) orig[1].getId()] = 1;
                mod++;
            }
        }
        
        /*
        for (VirtualLink vlinki : copy) {
            for (AbstractDemand ad : vlinki.get()) {
                for (Mapping map : ad.getMappings()) {
                    if (map.getResource().getOwner() instanceof SubstrateLink) {
                        SubstrateLink search = (SubstrateLink) map.getResource().getOwner();
                        SubstrateNode[] sNodes = new SubstrateNode[2];
                        int counter = 0;
                        for (SubstrateNode sn : stack.getSubstrate().getIncidentVertices(search)) {
                            sNodes[counter] = sn;
                            counter++;
                        }
                        VirtualNode[] vNodes = new VirtualNode[2];
                        int counter2 = 0;
                        for (VirtualNode vnn : stack.getVirtualNetworks().get(vlinki.getLayer() - 1).getIncidentVertices(vlinki)) {
                            vNodes[counter2] = vnn;
                            counter2++;
                        }
                        int equalizer = 0;
                        int firstElement = 0;
                        for (VirtualNetwork vnet : stack.getVirtualNetworks()) {
                            if (vnet.getLayer() == vlinki.getLayer()) {
                                firstElement = (int) vnet.getVertices().iterator().next().getId();
                                break;
                            } else {
                                for (VirtualLink vll : vnet.getEdges()) {
                                    equalizer++;
                                }
                            }
                        }
                        boolean contained = false;
                        for (SubstrateLink slinki : coll) {
                            if (slinki.getId() == search.getId()) {
                                coll.remove(slinki);
                                matrix[(int) sNodes[0].getId()][mod] = 1;
                                matrix[mod][(int) sNodes[0].getId()] = 1;
                                matrix[mod][(int) sNodes[1].getId()] = 1;
                                matrix[(int) sNodes[1].getId()][mod] = 1;
                                matrix[(int) (vNodes[0].getId() - firstElement + minVirtualNode - equalizer)][mod2] = 1;
                                matrix[mod2][(int) (vNodes[0].getId() - firstElement + minVirtualNode - equalizer)] = 1;
                                matrix[(int) (vNodes[1].getId() - firstElement + minVirtualNode - equalizer)][mod2] = 1;
                                matrix[mod2][(int) (vNodes[1].getId() - firstElement + minVirtualNode - equalizer)] = 1;
                                matrix[mod2][mod] = 1;
                                matrix[mod][mod2] = 1;
                                contained = true;
                                //mod++;
                                break;
                            }
                        }
                        if (!contained) {
                            int required = 0;
                            for (int k = minMetaSubstrate; k < maxMetaSubstrate; k++) {
                                if (matrix[(int) sNodes[0].getId()][k] == 1
                                        && matrix[(int) sNodes[1].getId()][k] == 1) {
                                    required = k;
                                    break;
                                }
                            }
                            
                            matrix[(int) (vNodes[0].getId() - firstElement + minVirtualNode - equalizer)][mod2] = 1;
                            matrix[mod2][(int) (vNodes[0].getId() - firstElement + minVirtualNode - equalizer)] = 1;
                            matrix[(int) (vNodes[1].getId() - firstElement + minVirtualNode - equalizer)][mod2] = 1;
                            matrix[mod2][(int) (vNodes[1].getId() - firstElement + minVirtualNode - equalizer)] = 1;
                            matrix[mod2][required] = 1;
                            matrix[required][mod2] = 1;
                            //mod2++;
                        }
                    }
                    mod++;
                }
            }
            mod2++;
        }*/
                
                /*
                int counter = minMetaSubstrate;
                
                if (i < numberOfSubstrateNodes) {
                    if (j < numberOfSubstrateNodes && i != j) {
                        SubstrateNode snode1 = null;
                        SubstrateNode snode2 = null;
                        for (SubstrateNode sn : stack.getSubstrate().getVertices()) {
                            if (sn.getId() == i) {
                                snode1 = sn;
                            } else if (sn.getId() == j) {
                                snode2 = sn;
                            }
                        }
                        SubstrateLink searchLink = null;
                        boolean isLink = false;
                        Collection<SubstrateLink> coll1 = stack.getSubstrate().getIncidentEdges(snode1);
                        Collection<SubstrateLink> coll2  = stack.getSubstrate().getIncidentEdges(snode2);
                        for (SubstrateLink slink : coll1) {
                            for (SubstrateLink sllink : coll2) {
                                if (slink.getId() == sllink.getId()) {
                                    searchLink = sllink;
                                    isLink = true;
                                    break;
                                }
                            }
                        }
                        if (isLink) {
                            boolean meta = false;
                            for (SubstrateLink sll : coll) {
                                if (sll.getId() == searchLink.getId()) {
                                    meta = true;
                                }
                            }
                            if (!meta) {
                                matrix[i][j] = 1;
                            } else {
                                matrix[i][counter] = 1;
                                matrix[counter][j] = 1;
                                matrix[counter][i] = 1;
                                matrix[j][counter] = 1;
                                counter++;
                                coll.remove(searchLink);
                            }
                        }
                                                
                    }
                } else if (i < maxVirtualNode) {
                    if (j < maxVirtualNode && i != j) {
                        int modifier = stack.getSubstrate().getEdgeCount();
                        VirtualNode vnode1 = null;
                        VirtualNode vnode2 = null;
                        int layer = 0;
                        for (VirtualNetwork vn : stack.getVirtualNetworks()) {
                            for (VirtualNode vnode : vn.getVertices()) {
                                if (vnode.getId() - countSubstrateLinks == i) {
                                    vnode1 = vnode;
                                } else if (vnode.getId() - countSubstrateLinks == j) {
                                    vnode2 = vnode;
                                }                                
                            }
                            if (vnode1 != null && vnode2 != null) {
                                layer = vn.getLayer();
                                break;
                            }
                        }
                        assert vnode1 != null && vnode2 != null;
                        VirtualLink searchLink = null;
                        boolean isLink = false;
                        Collection<VirtualLink> coll1 = stack.getVirtualNetworks().get(layer).getIncidentEdges(vnode1);
                        Collection<VirtualLink> coll2  = stack.getVirtualNetworks().get(layer).getIncidentEdges(vnode1);
                        for (VirtualLink vlink1 : coll1) {
                            for (VirtualLink vlink2 : coll2) {
                                if (vlink1.getId() == vlink2.getId()) {
                                    searchLink = vlink1;
                                    isLink = true;
                                    break;
                                }
                            }
                        }
                        if (isLink) {
                            matrix[i][counter] = 1;
                            matrix[counter][j] = 1;
                            counter++;
                            copy.remove(searchLink);
                            //stack.getVirtualNetworks().get(layer).removeEdge(searchLink);//KOPIE make of collections!!!
                            //begin with virtual links and their mapping so that first of all the meta nodes are coherent.
                            
                        }
                    }
                } else if (i >= minMetaSubstrate) {
                    if (j >= minMetaSubstrate) {
                        
                    }
                }
                      */         
      
        return matrix;
    }
    
    /**
     * This method is to generate a matrix out of the stack representing 
     * all virtual networks mapped onto the substrate network.
     * 
     * @param stack the stack to be transformed into a matrix representing
     * the mappings.
     * 
     * @return the matrix.
     */
    public static int[][] generateMappingMatrix(NetworkStack stack) {
        int count;
        if (!stack.getVirtualNetworks().isEmpty()) {
            count = 0;
        } else {
            count = 0;
        }

        for (Iterator<VirtualNetwork> vns = stack.getVirtualNetworks().iterator(); vns.hasNext();) {
            
            VirtualNetwork vn = vns.next();
            
            for (Iterator<VirtualNode> vnode = vn.getVertices().iterator(); vnode.hasNext();) {
                vnode.next();
                count += 1;
            }
            
        }

        int size = stack.getSubstrate().getSize();
        int totalSize = size + count;
        
        int[][] matrix = new int[totalSize][totalSize];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                Node  node1 = null;
                Node node2 = null;
                Collection<SubstrateLink> a = null;
                Collection<SubstrateLink> slinki = null;
                Collection<VirtualLink> b = null;
                Collection<VirtualLink> vlinki = null;
                VirtualNetwork vn1 = null;
                VirtualNetwork vn2 = null;
                
                
                if (i < size && j < size) {
                    //stream substrate
                    //node1 = new SubstrateNode();
                    for (SubstrateNode snode : stack.getSubstrate().getVertices()) {
                        if (snode.getId() == i) {
                            node1 = snode;
                          //stack.getSubstrate().
                            Collection<SubstrateNode> coll = stack.getSubstrate().getSuccessors((SubstrateNode) node1);
                            for (SubstrateNode snode2 : coll) {
                                if (snode2.getId() == j) {
                                    matrix[i][j] = 1;
                                }
                            }
                            //slinki = stack.getSubstrate().getIncidentEdges(snode);
                            //break;
                        }
                    }
                } else if (i >= size && j >= size) {
                    //stream virtual networks               
                    //node1 = new VirtualNode();
                    boolean runner = true;
                    int counter = i - size;
                    for (VirtualNetwork vns : stack.getVirtualNetworks()) {
                        for (VirtualNode vnn : vns.getVertices()) {
                            if (counter == 0 && runner) {
                                node1 = vnn;
                                long pos = vnn.getId();
                                int sizevn = vns.getSize();
                                Collection<VirtualNode> coll = vns.getSuccessors(vnn);
                                for (AbstractDemand ad : vnn.get()) {
                                    for (Mapping map : ad.getMappings()) {
                                        if (map.getResource().getOwner() instanceof  SubstrateNode) {
                                            int snodeid = (int) map.getResource().getOwner().getId();
                                            matrix[i][snodeid] = 1;
                                        }
                                    }
                                }
                                for (VirtualNode vnode : coll) {
                                    long pos2 = vnode.getId();
                                    if (pos2 > pos) {
                                        int diff = (int) (pos2 - pos);
                                        matrix[i][i + diff] = 1;
                                    } else if (pos > pos2) {
                                        int diff = (int) (pos - pos2);
                                        matrix[i][i - diff] = 1;
                                    }
                                }
                                runner = false;
                            } else {
                                counter--;
                            }
                        }
                    }
                    
                    /*
                    for (VirtualNode vrnode : vn.getVertices()) {
                        for (AbstractDemand ad : vrnode.get()) {
                            for (Mapping map : ad.getMappings()) {
                                if (map.getResource().getOwner() instanceof SubstrateNode) {
                                    long snodeid = map.getResource().getOwner().getId();
                                    for (SubstrateNode snn : stack.getSubstrate().getVertices()) {
                                        if (snn.getId() == snodeid) {
                                            
                                        }
                                    }
                                }
                            }
                        }
                    }*/
                    
                    /*
                    boolean runner = true;
                    boolean runner2 = true;
                    int counter = i - size;
                    
                    for (VirtualNetwork vns : stack.getVirtualNetworks()) {  
                        int counterj = j - size;
                        //stream virtual nodes
                        for (VirtualNode vnn : vns.getVertices()) {
                            if (counter == 0 && runner) {
                                node1 = vnn;
                                Collection<VirtualNode> coll = vns.getSuccessors(vnn);
                                //null?
                                for (VirtualNode vnode : coll) {
                                    if (counterj == 0 && runner2) {
                                        matrix[i][j] = 1;
                                        runner2 = false;
                                    } else {
                                        counterj--;
                                    }
                                }
                                //vlinki = vns.getIncidentEdges(vnn);
                                runner = false;
                                //break;
                            } else {
                                counter--;
                                
                            }
                        }
                    }*/                    
                    
                } else {
                    matrix[i][j] = 0;
                }               
                
                        
            }
            
        }
        return matrix;
    }

}
