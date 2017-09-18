# GraphViewDemo

This app shows the evolution of writing a custom view by creating a graph view that takes in an array of floats and draws either a line or bar graph. The graph view also accepts touch inputs and draws a pointer with animations.

Each GraphView builds off of the previous one.

## Steps:
1. [GraphView](../master/app/src/main/java/com/redkb/graphviewdemo/GraphView.java) - overrides onDraw to draw two lines to make the axes of the graph
2. [GraphView2](../master/app/src/main/java/com/redkb/graphviewdemo/GraphView2.java) - adds public setData method and uses a Path object to draw the line graph
3. [GraphView3](../master/app/src/main/java/com/redkb/graphviewdemo/GraphView3.java) - overrides onTouchEvent and draws a pointer for where the user is touching
4. [GraphView4](../master/app/src/main/java/com/redkb/graphviewdemo/GraphView4.java) - adds animation with ValueAnimator to animate the pointer when a user touches the graph
5. [GraphView5](../master/app/src/main/java/com/redkb/graphviewdemo/GraphView5.java) - adds custom attributes to control the graph view in xml and adds a bar type graph that can be selected with custom attribute



Created for a talk given at the [OC Android Developer](https://www.meetup.com/OC-Android-Developers/) Meetup group.

Slides for this talk will be posted here once they have been written.
