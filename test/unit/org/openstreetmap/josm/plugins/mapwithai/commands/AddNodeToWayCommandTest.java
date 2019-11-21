// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.mapwithai.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openstreetmap.josm.TestUtils;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.testutils.JOSMTestRules;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class AddNodeToWayCommandTest {
    private Node toAdd;
    private Way way;
    private AddNodeToWayCommand command;
    @Rule
    @SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
    public JOSMTestRules test = new JOSMTestRules().projection();

    @Before
    public void setupArea() {
        toAdd = new Node(new LatLon(0, 0));
        way = TestUtils.newWay("", new Node(new LatLon(0.1, 0.1)), new Node(new LatLon(-0.1, -0.1)));
        new DataSet(toAdd, way.firstNode(), way.lastNode(), way);
        command = new AddNodeToWayCommand(toAdd, way, way.firstNode(), way.lastNode());
    }

    @Test
    public void testAddNodeToWay() {
        command.executeCommand();
        Assert.assertEquals(3, way.getNodesCount());

        command.undoCommand();
        Assert.assertEquals(2, way.getNodesCount());

        command = new AddNodeToWayCommand(toAdd, way, way.lastNode(), way.firstNode());

        command.executeCommand();
        Assert.assertEquals(3, way.getNodesCount());

        command.undoCommand();
        Assert.assertEquals(2, way.getNodesCount());
    }

    @Test
    public void testDescription() {
        Assert.assertNotNull(command.getDescriptionText());
    }

    @Test
    public void testModifiedAddedDeleted() {
        final List<OsmPrimitive> added = new ArrayList<>();
        final List<OsmPrimitive> modified = new ArrayList<>();
        final List<OsmPrimitive> deleted = new ArrayList<>();
        command.fillModifiedData(modified, deleted, added);
        Assert.assertTrue(deleted.isEmpty());
        Assert.assertTrue(added.isEmpty());
        Assert.assertEquals(2, modified.size());
    }

    @Test
    public void testMultiAddConnections() {
        command.executeCommand();
        Node tNode = new Node(new LatLon(0.01, 0.01));
        way.getDataSet().addPrimitive(tNode);
        command = new AddNodeToWayCommand(tNode, way, way.firstNode(), way.lastNode());
        command.executeCommand();
        assertEquals(new LatLon(0.1, 0.1), way.firstNode().getCoor());
        assertEquals(new LatLon(0.01, 0.01), way.getNode(1).getCoor());
        assertEquals(new LatLon(0, 0), way.getNode(2).getCoor());
        assertEquals(new LatLon(-0.1, -0.1), way.lastNode().getCoor());
        command.undoCommand();
        tNode.setCoor(new LatLon(-0.01, -0.01));
        command = new AddNodeToWayCommand(tNode, way, way.firstNode(), way.lastNode());
        command.executeCommand();
        assertEquals(new LatLon(0.1, 0.1), way.firstNode().getCoor());
        assertEquals(new LatLon(0, 0), way.getNode(1).getCoor());
        assertEquals(new LatLon(-0.01, -0.01), way.getNode(2).getCoor());
        assertEquals(new LatLon(-0.1, -0.1), way.lastNode().getCoor());
    }
}
