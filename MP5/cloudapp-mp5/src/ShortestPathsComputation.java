import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.conf.LongConfOption;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;

import java.io.IOException;

/**
 * Compute shortest paths from a given source.
 */
public class ShortestPathsComputation extends BasicComputation<
    IntWritable, IntWritable, NullWritable, IntWritable> {
  /** The shortest paths id */
  public static final LongConfOption SOURCE_ID =
      new LongConfOption("SimpleShortestPathsVertex.sourceId", 1,
          "The shortest paths id");

  /**
   * Is this vertex the source id?
   *
   * @param vertex Vertex
   * @return True if the source id
   */
  private boolean isSource(Vertex<IntWritable, ?, ?> vertex) {
    return vertex.getId().get() == SOURCE_ID.get(getConf());
  }

  @Override
  public void compute(Vertex<IntWritable,IntWritable,NullWritable> vertex,Iterable<IntWritable> messages)throws IOException{
	int currentLength = vertex.getValue().get();
	// First superstep is special, because we can simply look at the neighbors
	if (getSuperstep()==0){
		if(isSource(vertex)){
			vertex.setValue(new IntWritable(0));
			sendMessageToAllEdges(vertex, vertex.getValue());
		}else{
			vertex.setValue(new IntWritable(Integer.MAX_VALUE));
		}
		vertex.voteToHalt();
		return;
	}

	boolean changed = false;
	// did we get a smaller length ?
	for(IntWritable message:messages){
		int candidateLength = message.get()+1;
		if (candidateLength<currentLength){
			currentLength=candidateLength;
			changed=true; 
		}
	}

	// propagate new length to the neighbors
	if(changed){
		vertex.setValue(new IntWritable(currentLength)); 
		sendMessageToAllEdges(vertex, vertex.getValue());
	}
	vertex.voteToHalt(); 
  }
}


