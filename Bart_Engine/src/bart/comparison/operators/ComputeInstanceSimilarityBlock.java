package bart.comparison.operators;

import bart.comparison.ComparisonConfiguration;
import bart.comparison.ComparisonStats;
import bart.comparison.ComparisonUtility;
import bart.comparison.CompatibilityMap;
import bart.comparison.InstanceMatchTask;
import bart.comparison.SignatureAttributes;
import bart.comparison.SignatureMap;
import bart.comparison.SignatureMapCollection;
import bart.comparison.TupleMatch;
import bart.comparison.TupleMatches;
import bart.comparison.TupleSignature;
import bart.comparison.ValueMappings;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.JFrame;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.IDatabase;
import speedy.model.database.IValue;
import speedy.model.database.Tuple;
import speedy.model.database.TupleWithTable;
import speedy.utility.SpeedyUtility;

public class ComputeInstanceSimilarityBlock implements IComputeInstanceSimilarity {

    private final static Logger logger = LoggerFactory.getLogger(ComputeInstanceSimilarityBlock.class);
//    private final SignatureMapCollectionGenerator signatureGenerator = new SignatureMapCollectionGenerator();
//    private final CheckTupleMatch tupleMatcher = new CheckTupleMatch();
//    private final CheckTupleMatchCompatibility compatibilityChecker = new CheckTupleMatchCompatibility();
    private final FindCompatibleTuples compatibleTupleFinder = new FindCompatibleTuples();

    @Override
    public InstanceMatchTask compare(IDatabase leftDb, IDatabase rightDb) {
        if (!ComparisonConfiguration.isInjective() || !ComparisonConfiguration.isFunctional()) {
            throw new IllegalArgumentException("Only fully-injective mappings are supported");
        }
        long start = System.currentTimeMillis();
        InstanceMatchTask instanceMatch = new InstanceMatchTask(this.getClass().getSimpleName(), leftDb, rightDb);
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabase(leftDb);
        List<TupleWithTable> destinationTuples = SpeedyUtility.extractAllTuplesFromDatabase(rightDb);
        ComparisonStats.getInstance().addStat(ComparisonStats.PROCESS_INSTANCE_TIME, System.currentTimeMillis() - start);
        UndirectedGraph<TupleWithTable, DefaultEdge> instancesGraph = new SimpleGraph<TupleWithTable, DefaultEdge>(DefaultEdge.class);
        start = System.currentTimeMillis();
        addInstance(sourceTuples, instancesGraph);
        addInstance(destinationTuples, instancesGraph);
        ComparisonStats.getInstance().addStat(ComparisonStats.BUILD_INSTANCES_GRAPH, System.currentTimeMillis() - start);
        CompatibilityMap compatibilityMap = compatibleTupleFinder.find(sourceTuples, destinationTuples);
        TupleMatches tupleMatches = ComparisonUtility.findTupleMatches(destinationTuples, compatibilityMap);
        start = System.currentTimeMillis();
        addTupleMatches(tupleMatches, instancesGraph);
        ComparisonStats.getInstance().addStat(ComparisonStats.BUILD_INSTANCES_GRAPH, System.currentTimeMillis() - start);
//        findCompatibileTuples(sourceTuples, destinationTuples, instancesGraph);
//        findCompatibileTuples(destinationTuples, sourceTuples, instancesGraph);
//        saveGraph(instancesGraph);
//        TupleMatches tupleMatches = findTupleMatches(sourceTuples, destinationTuples);
//        ComparisonUtility.sortTupleMatches(tupleMatches);
//        if (logger.isTraceEnabled()) logger.trace(tupleMatches.toString());
//        TupleMapping bestTupleMapping = bestTupleMappingFinder.findBestTupleMapping(sourceTuples, destinationTuples, tupleMatches);
//        nonMatchingTuplesFinder.find(sourceTuples, destinationTuples, bestTupleMapping);
//        instanceMatch.setTupleMapping(bestTupleMapping);
        return instanceMatch;
    }

    private void addInstance(List<TupleWithTable> tuples, UndirectedGraph<TupleWithTable, DefaultEdge> instancesGraph) {
        Map<IValue, Set<TupleWithTable>> placeholdersInverseMap = new HashMap<>();
        for (TupleWithTable tuple : tuples) {
            instancesGraph.addVertex(tuple);
            addPlaceholders(tuple, placeholdersInverseMap);
        }
        for (IValue placeholder : placeholdersInverseMap.keySet()) {
            Set<TupleWithTable> tuplesWithPlaceholder = placeholdersInverseMap.get(placeholder);
            addEdgesBtwTuples(tuplesWithPlaceholder, instancesGraph);
        }
    }

    private void addTupleMatches(TupleMatches tupleMatches, UndirectedGraph<TupleWithTable, DefaultEdge> instancesGraph) {
        for (TupleWithTable tuple : tupleMatches.getTuples()) {
            List<TupleMatch> matchesForTuple = tupleMatches.getMatchesForTuple(tuple);
            addEdgesBtwMatchingTuples(matchesForTuple, instancesGraph);
        }
    }

//    private void findCompatibileTuples(List<TupleWithTable> srcTuples, List<TupleWithTable> destTuples, UndirectedGraph<TupleWithTable, DefaultEdge> instancesGraph) {
//        SignatureMapCollection srcSignatureMap = signatureGenerator.generateIndexForTuples(srcTuples);
//        for (TupleWithTable destTuple : destTuples) {
//            if (logger.isDebugEnabled()) logger.debug("Finding a tuple that can be mapped in " + destTuple);
//            List<SignatureAttributes> signatureAttributesForTable = srcSignatureMap.getRankedAttributesForTable(destTuple.getTable());
//            if (logger.isDebugEnabled()) logger.debug("Signature for table " + destTuple.getTable() + ": " + signatureAttributesForTable);
//            List<TupleMatch> matchingTuples = findMatchingTuples(destTuple, signatureAttributesForTable, srcSignatureMap);
//            if (logger.isDebugEnabled()) logger.debug("Possible matching tuples: " + matchingTuples);
//            addEdgesBtwCompatibileTuples(matchingTuples, instancesGraph);
//        }
//    }
//    private List<TupleMatch> findMatchingTuples(TupleWithTable destinationTuple, List<SignatureAttributes> signatureAttributesForTable, SignatureMapCollection leftSignatureMaps) {
//        List<TupleMatch> matchingTuples = new ArrayList<TupleMatch>();
//        Set<AttributeRef> attributesWithGroundValues = ComparisonUtility.findAttributesWithGroundValue(destinationTuple.getTuple());
//        for (SignatureAttributes signatureAttribute : signatureAttributesForTable) {
//            if (logger.isTraceEnabled()) logger.trace("Checking signature attribute " + signatureAttribute);
//            if (!ComparisonUtility.isCompatible(attributesWithGroundValues, signatureAttribute.getAttributes())) {
//                if (logger.isTraceEnabled()) logger.trace("Skipping not compatible signature attribute " + signatureAttribute);
//                continue;
//            }
//            SignatureMap signatureMap = leftSignatureMaps.getSignatureForAttributes(signatureAttribute);
//            TupleSignature rightTupleSignature = signatureGenerator.generateSignature(destinationTuple, signatureAttribute.getAttributes());
//            List<Tuple> tuplesWithSameSignature = signatureMap.getTuplesForSignature(rightTupleSignature.getSignature());
//            if (tuplesWithSameSignature == null || tuplesWithSameSignature.isEmpty()) {
//                continue;
//            }
//            for (Iterator<Tuple> it = tuplesWithSameSignature.iterator(); it.hasNext();) {
//                Tuple srcTuple = it.next();
//                TupleWithTable srcTupleWithTable = new TupleWithTable(destinationTuple.getTable(), srcTuple);
//                TupleMatch tupleMatch = tupleMatcher.checkMatch(srcTupleWithTable, destinationTuple);
//                if (tupleMatch == null) {
//                    continue;
//                }
//                boolean compatible = compatibilityChecker.checkCompatibilityAndMerge(new ValueMappings(), tupleMatch);
//                if (!compatible) {
//                    continue;
//                }
//                matchingTuples.add(tupleMatch);
//            }
//        }
//        return matchingTuples;
//    }
    private void addPlaceholders(TupleWithTable tuple, Map<IValue, Set<TupleWithTable>> placeholdersInverseMap) {
        for (Cell cell : tuple.getTuple().getCells()) {
            if (cell.isOID()) {
                continue;
            }
            IValue value = cell.getValue();
            if (!SpeedyUtility.isPlaceholder(value)) {
                continue;
            }
            Set<TupleWithTable> tuplesWithPlaceholder = placeholdersInverseMap.getOrDefault(value, new HashSet<TupleWithTable>());
            tuplesWithPlaceholder.add(tuple);
            placeholdersInverseMap.put(value, tuplesWithPlaceholder);
        }
    }

    private void addEdgesBtwTuples(Set<TupleWithTable> tuplesWithPlaceholder, UndirectedGraph<TupleWithTable, DefaultEdge> instancesGraph) {
        List<TupleWithTable> list = new ArrayList<>(tuplesWithPlaceholder);
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                TupleWithTable tupleA = list.get(i);
                TupleWithTable tupleB = list.get(j);
                if (instancesGraph.getEdge(tupleA, tupleB) == null) {
                    instancesGraph.addEdge(tupleA, tupleB);
                }
            }
        }
    }

    private void addEdgesBtwMatchingTuples(List<TupleMatch> matchingTuples, UndirectedGraph<TupleWithTable, DefaultEdge> instancesGraph) {
        for (TupleMatch matchingTuple : matchingTuples) {
            if (instancesGraph.getEdge(matchingTuple.getLeftTuple(), matchingTuple.getRightTuple()) == null) {
                instancesGraph.addEdge(matchingTuple.getLeftTuple(), matchingTuple.getRightTuple());
            }
        }
    }

    private void saveGraph(UndirectedGraph<TupleWithTable, DefaultEdge> instancesGraph) {
        JGraphXAdapter<TupleWithTable, DefaultEdge> jgxAdapterContext = new JGraphXAdapter<TupleWithTable, DefaultEdge>(instancesGraph);
        jgxAdapterContext.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_NOLABEL, "1");
        jgxAdapterContext.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_ENDARROW, "0");
        jgxAdapterContext.setCellsEditable(false);
        jgxAdapterContext.setCellsMovable(false);
        jgxAdapterContext.setEdgeLabelsMovable(false);
        jgxAdapterContext.setCellsDeletable(false);
        jgxAdapterContext.setCellsDisconnectable(false);
        jgxAdapterContext.setCellsResizable(false);
        jgxAdapterContext.setCellsBendable(false);
        JFrame frame = new JFrame();
        mxGraphComponent mxGraphComponent = new mxGraphComponent(jgxAdapterContext);
        frame.getContentPane().add(mxGraphComponent, BorderLayout.CENTER);
        mxHierarchicalLayout layout = new mxHierarchicalLayout(jgxAdapterContext);
        layout.execute(jgxAdapterContext.getDefaultParent());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Graph");
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        try {
            while (true) {
                Thread.sleep(500);
            }
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(ComputeInstanceSimilarityBlock.class.getName()).log(Level.SEVERE, null, ex);
        }
//        try {
//            BufferedImage image = mxCellRenderer.createBufferedImage(jgxAdapterContext, null, 1, Color.WHITE, true, null);
//            ImageIO.write(image, "PNG", new File("/Temp/bart/similarity/instances-graph.png"));
//        } catch (IOException ex) {
//            logger.error("Unable to save graph image: " + ex.getLocalizedMessage());
//        }
    }

}
