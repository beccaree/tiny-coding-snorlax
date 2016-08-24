package scheduling_solution.visualisation;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Date;
import java.util.List;

import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTick;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.Tick;
import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Year;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;


/**
 *Since JFreeChart's default DateAxis class doesn't provide sufficient support for customising axis labels,
 * TimeAxis overrides some of its methods to do so.
 */
@SuppressWarnings({ "serial", "rawtypes", "unchecked", "deprecation" })
public class TimeAxis extends DateAxis {
	
	DateTickUnit tickUnit = super.getTickUnit();
	DateTickMarkPosition tickMarkPosition = super.getTickMarkPosition();
	
	public TimeAxis() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see org.jfree.chart.axis.DateAxis#refreshTicksHorizontal(java.awt.Graphics2D, java.awt.geom.Rectangle2D, org.jfree.ui.RectangleEdge)
	 */
	@Override
	protected List refreshTicksHorizontal(Graphics2D g2,
                                           Rectangle2D dataArea,
                                           RectangleEdge edge) {
 
         List result = new java.util.ArrayList();
 
         Font tickLabelFont = getTickLabelFont();
         g2.setFont(tickLabelFont);
 
         if (isAutoTickUnitSelection()) {
             selectAutoTickUnit(g2, dataArea, edge);
         }
 
         DateTickUnit unit = getTickUnit();
         Date tickDate = calculateLowestVisibleTickValue(unit);
         Date upperDate = getMaximumDate();
         // float lastX = Float.MIN_VALUE;
         while (tickDate.before(upperDate)) {
 
             if (!isHiddenValue(tickDate.getTime())) {
                 // work out the value, label and position
                 String tickLabel;
                 
                 /*---------------------------ADDITIONAL PART---------------------------*/
                 // Convert Date object to time units and draw time axis labels accordingly
                 int hours = tickDate.getHours();
                 int mins = tickDate.getMinutes();
                 int secs = tickDate.getSeconds();
                 int timeInSecs = secs + (60*mins) + (60*60*hours);
                 
                 tickLabel = timeInSecs+"";
                 
                 TextAnchor anchor = null;
                 TextAnchor rotationAnchor = null;
                 double angle = 0.0;
                 if (isVerticalTickLabels()) {
                     anchor = TextAnchor.CENTER_RIGHT;
                     rotationAnchor = TextAnchor.CENTER_RIGHT;
                     if (edge == RectangleEdge.TOP) {
                         angle = Math.PI / 2.0;
                     }
                     else {
                         angle = -Math.PI / 2.0;
                     }
                 }
                 else {
                     if (edge == RectangleEdge.TOP) {
                         anchor = TextAnchor.BOTTOM_CENTER;
                         rotationAnchor = TextAnchor.BOTTOM_CENTER;
                     }
                     else {
                         anchor = TextAnchor.TOP_CENTER;
                         rotationAnchor = TextAnchor.TOP_CENTER;
                     }
                 }
 
                 Tick tick = new DateTick(
                     tickDate, tickLabel, anchor, rotationAnchor, angle
                 );
                 result.add(tick);
                 tickDate = unit.addToDate(tickDate);
             }
             else {
                 tickDate = unit.rollDate(tickDate);
                 continue;
             }
 
             // could add a flag to make the following correction optional...
             switch (unit.getUnit()) {
 
                 case (DateTickUnit.MILLISECOND) :
                 case (DateTickUnit.SECOND) :
                 case (DateTickUnit.MINUTE) :
                 case (DateTickUnit.HOUR) :
                 case (DateTickUnit.DAY) :
                     break;
                 case (DateTickUnit.MONTH) :
                     tickDate = calculateDateForPosition(new Month(tickDate), 
                             this.tickMarkPosition);
                     break;
                 case(DateTickUnit.YEAR) :
                     tickDate = calculateDateForPosition(
                             new Year(tickDate), this.tickMarkPosition);
                     break;
 
                 default: break;
 
             }
 
         }
         return result;
 
     }
	
	/* (non-Javadoc)
     * @see org.jfree.chart.axis.DateAxis#calculateDateForPosition(RegularTimePeriod period, 
       DateTickMarkPosition position)
     */
     /**
     * This class replaces a method from DateAxis that is set to private to be used by this class
     */
     private Date calculateDateForPosition(RegularTimePeriod period, 
                                           DateTickMarkPosition position) {
         
         if (position == null) {
             throw new IllegalArgumentException("Null 'position' argument.");   
         }
         Date result = null;
         if (position == DateTickMarkPosition.START) {
             result = new Date(period.getFirstMillisecond());
         }
         else if (position == DateTickMarkPosition.MIDDLE) {
             result = new Date(period.getMiddleMillisecond());
         }
         else if (position == DateTickMarkPosition.END) {
             result = new Date(period.getLastMillisecond());
         }
         return result;
 
     }

}
