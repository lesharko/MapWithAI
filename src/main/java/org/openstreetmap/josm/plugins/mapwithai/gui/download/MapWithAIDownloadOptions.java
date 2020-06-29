// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.mapwithai.gui.download;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.gui.bbox.BBoxChooser;
import org.openstreetmap.josm.gui.download.DownloadDialog;
import org.openstreetmap.josm.gui.download.DownloadSelection;
import org.openstreetmap.josm.plugins.mapwithai.gui.preferences.mapwithai.MapWithAIProvidersPanel;
import org.openstreetmap.josm.tools.Destroyable;
import org.openstreetmap.josm.tools.GBC;

/**
 * Add options for MapWithAI downloads to the main JOSM download window
 *
 * @author Taylor Smock
 */
public class MapWithAIDownloadOptions extends JPanel implements DownloadSelection, Destroyable, PropertyChangeListener {
    private final JPanel optionPanel;
    private DownloadDialog iGui;
    private final MapWithAIProvidersPanel mapwithaiProvidersPanel;

    public MapWithAIDownloadOptions() {
        optionPanel = new JPanel(new GridBagLayout());
        JPanel infoHeader = new JPanel();
        infoHeader.add(new JLabel("Browse and activate extra data sets to facilitate your mapping needs."));
        optionPanel.add(infoHeader, GBC.eol().fill(GBC.HORIZONTAL).anchor(GBC.NORTH));
        mapwithaiProvidersPanel = new MapWithAIProvidersPanel(this);
        optionPanel.add(mapwithaiProvidersPanel, GBC.eol().fill(GBC.BOTH).anchor(GBC.CENTER));
        mapwithaiProvidersPanel.defaultMap.addPropertyChangeListener(this);
    }

    @Override
    public void addGui(DownloadDialog gui) {
        iGui = gui;
        iGui.addDownloadAreaSelector(optionPanel, tr("Browse MapWithAI Data Sources"));
        iGui.addDownloadAreaListener(this);
    }

    @Override
    public void setDownloadArea(Bounds area) {
        // This is (currently) never called.
        // See https://josm.openstreetmap.de/ticket/19310
        mapwithaiProvidersPanel.setCurrentBounds(area);
    }

    @Override
    public void destroy() {
        if (this.iGui != null) {
            this.iGui.remove(this);
            this.iGui.removeDownloadAreaListener(this);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(BBoxChooser.BBOX_PROP) && iGui != null) {
            mapwithaiProvidersPanel.fireAreaListeners();
            iGui.boundingBoxChanged((Bounds) evt.getNewValue(), this);
        }
    }
}
