
/* 
  SWAMP Jenkins Plugin

  Copyright 2016 Jared Sweetland, Vamshi Basupalli, James A. Kupsch

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  */

package org.continuousassurance.swamp.jenkins;

import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.graph.CategoryBuildResultGraph;
import hudson.plugins.analysis.graph.ColorPalette;
import hudson.plugins.analysis.graph.GraphConfiguration;
import hudson.plugins.analysis.util.BoxRenderer;
import hudson.plugins.analysis.util.CategoryUrlBuilder;
import hudson.plugins.analysis.util.ToolTipBoxRenderer;
import hudson.plugins.analysis.util.ToolTipBuilder;
import hudson.plugins.analysis.util.ToolTipProvider;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;

import org.continuousassurance.swamp.Messages;

/**
 * Builds a review count graph for a specified result action.
 *
 * @author Keith Lea
 */
public class SwampEvaluationsGraph extends CategoryBuildResultGraph {
    @Override
    public String getId() {
        return "EVALS";
    }

    @Override
    public String getLabel() {
        return /*hudson.plugins.findbugs.*/Messages.FindBugs_EvaluationsGraph_title();
    	//return "hudson.plugins.findbugs.Messages.FindBugs_EvaluationsGraph_title";
    }

    @Override
    protected List<Integer> computeSeries(final BuildResult current) {
        List<Integer> series = new ArrayList<Integer>();
        if (current instanceof SwampResult) {
            SwampResult findBugsResult = (SwampResult) current;
            series.add(findBugsResult.getNumberOfComments());
        }
        return series;
    }

    @Override
    protected JFreeChart createChart(final CategoryDataset dataSet) {
        return createBlockChart(dataSet);
    }

    @Override
    protected Color[] getColors() {
        return new Color[] {ColorPalette.BLUE};
    }

    @Override
    protected CategoryItemRenderer createRenderer(final GraphConfiguration configuration, final String pluginName, final ToolTipProvider toolTipProvider) {
        CategoryUrlBuilder url = new UrlBuilder(getRootUrl(), pluginName);
        ToolTipBuilder toolTip = new DescriptionBuilder(toolTipProvider);
        
        if (configuration.useBuildDateAsDomain()) {
            return new ToolTipBoxRenderer(toolTip);
        }
        else {
            return new BoxRenderer(url, toolTip);
        }
    }

    /**
     * Shows a tooltip.
     */
    private static final class DescriptionBuilder extends ToolTipBuilder {
        private static final long serialVersionUID = -223463531447822459L;

        DescriptionBuilder(final ToolTipProvider provider) {
            super(provider);
        }

            @Override
        protected String getShortDescription(final int row) {
            if (row == 1) {
                return Messages.Trend_Fixed();
            }
            else {
                return Messages.Trend_New();
            }
        }
    }

    /**
     * Shows a URL.
     */
    private static final class UrlBuilder extends CategoryUrlBuilder {
        private static final long serialVersionUID = 6928145843235050754L;

        UrlBuilder(final String rootUrl, final String pluginName) {
            super(rootUrl, pluginName);
        }

            @Override
        protected String getDetailUrl(final int row) {
            if (row == 1) {
                return "fixed";
            }
            else {
                return "new";
            }
        }
    }
}
