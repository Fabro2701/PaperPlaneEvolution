package view.opengl.engineTester;



import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import model.grammar.Chromosome;
import model.grammar.StandardGrammar;
import model.grammar.AbstractGrammar.Symbol;
import model.plane.BasicPlane;
import model.plane.shapes.BasicPlaneShape;
import util.RandomSingleton;
import util.Triangle;
import view.opengl.entities.Camera;
import view.opengl.entities.Entity;
import view.opengl.models.RawModel;
import view.opengl.models.TexturedModel;
import view.opengl.renderEngine.DisplayManager;
import view.opengl.renderEngine.Loader;
import view.opengl.renderEngine.Renderer;
import view.opengl.shaders.StaticShader;
import view.opengl.textures.ModelTexture;


public class MainGameLoop {
	protected static class Atts{
		float[] vertices, textureCoords;
		int[] indices;
	}
	public static Atts generatePlane() {
		
		RandomSingleton.setSeed(1350L);
		Chromosome<Chromosome.Codon> c = new Chromosome<>(500, Chromosome.Codon::new);
		StandardGrammar grammar = new StandardGrammar();
		grammar.parseBNF("resources/grammar/default.bnf");
		LinkedList<Symbol>ss = (LinkedList<Symbol>) grammar.mapChromosome(c);
		StringBuilder sb = new StringBuilder();
		for(Symbol symb:ss)sb.append(symb.getName());
		
		System.out.println(sb.toString());

		
		
		//String test = "plane(40,0,45,10);cuad(upperRightCorner,+(upperRightCorner,N(0,0,5)),+(upperRightCorner,N(5,-3,7)),endBase);";
		String test = "plane(40,0,45,10);tri(upperRightCorner,+(upperRightCorner,N(5,-3,7)),endBase);tri(upperRightCorner,+(upperRightCorner,N(0,0,5)),+(upperRightCorner,N(5,-3,7)));";
/*
(0.0, 0.0, 0.0) , (0.0, 10.0, 9.999999999999998) , (40.0, 0.0, 0.0)
(0.0, 10.0, -9.999999999999998) , (40.0, 0.0, 0.0) , (0.0, 0.0, 0.0)
(40.0, 0.0, 0.0) , (5.0, 7.0, 17.0) , (0.0, 10.0, 9.999999999999998)
(0.0, 10.0, 9.999999999999998) , (0.0, 10.0, 14.999999999999998) , (5.0, 7.0, 17.0)
 */
		BasicPlane plane2 = BasicPlaneShape.parsePlaneAndShapes(sb.toString());
		//BasicPlane plane2 = BasicPlaneShape.parsePlaneAndShapes(test);
		plane2.getTriangles().stream().forEach(s -> System.out.println(s));
		

		Atts atts = new Atts();
		atts.vertices = new float[plane2.getTriangles().size()*3*3];
		atts.textureCoords = new float[plane2.getTriangles().size()*3*2];
		atts.indices = new int[plane2.getTriangles().size()*3];
		
		List<Triangle> tris = plane2.getTriangles();
		for(int i=0;i<tris.size()*3;i++) {
			atts.indices[i]=i;
		}
		for(int i=0;i<tris.size();i++) {
			
			atts.textureCoords[6*i]=0;
			atts.textureCoords[6*i+1]=0;
			atts.textureCoords[6*i+2]=0;
			atts.textureCoords[6*i+3]=1;
			atts.textureCoords[6*i+4]=1;
			atts.textureCoords[6*i+5]=1;
			
			for(int j=0;j<3;j++) {
				atts.vertices[9*i+3*j]=(float)tris.get(i).points[j].x;
				atts.vertices[9*i+3*j+1]=(float)tris.get(i).points[j].y;
				atts.vertices[9*i+3*j+2]=(float)tris.get(i).points[j].z;
			}
			
		}
		return atts;
	}
	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		StaticShader shader = new StaticShader();
		Renderer renderer = new Renderer(shader);
		
		
		Atts atts = MainGameLoop.generatePlane();
		float[] vertices = atts.vertices;
		int[] indices = atts.indices;
		float[] textureCoords = atts.textureCoords;
//		float[] vertices = {			
//				-0.5f,0.5f,0,	
//				-0.5f,-0.5f,0,	
//				0.5f,-0.5f,0,	
//				0.5f,0.5f,0,		
//				
//				-0.5f,0.5f,1,	
//				-0.5f,-0.5f,1,	
//				0.5f,-0.5f,1,	
//				0.5f,0.5f,1,
//				
//				0.5f,0.5f,0,	
//				0.5f,-0.5f,0,	
//				0.5f,-0.5f,1,	
//				0.5f,0.5f,1,
//				
//				-0.5f,0.5f,0,	
//				-0.5f,-0.5f,0,	
//				-0.5f,-0.5f,1,	
//				-0.5f,0.5f,1,
//				
//				-0.5f,0.5f,1,
//				-0.5f,0.5f,0,
//				0.5f,0.5f,0,
//				0.5f,0.5f,1,
//				
//				-0.5f,-0.5f,1,
//				-0.5f,-0.5f,0,
//				0.5f,-0.5f,0,
//				0.5f,-0.5f,1
//				
//		};
//		
//		float[] textureCoords = {
//				
//				0,0,
//				0,1,
//				1,1,
//				1,0,			
//				0,0,
//				0,1,
//				1,1,
//				1,0,			
//				0,0,
//				0,1,
//				1,1,
//				1,0,
//				0,0,
//				0,1,
//				1,1,
//				1,0,
//				0,0,
//				0,1,
//				1,1,
//				1,0,
//				0,0,
//				0,1,
//				1,1,
//				1,0
//
//				
//		};
//		
//		int[] indices = {
//				0,1,3,	
//				3,1,2,	
//				4,5,7,
//				7,5,6,
//				8,9,11,
//				11,9,10,
//				12,13,15,
//				15,13,14,	
//				16,17,19,
//				19,17,18,
//				20,21,23,
//				23,21,22
//
//		};
		/*float[] vertices = {			
				0,0,0,	
				40f,0,0,
				0,10f,10f,
				
				0,10f,-10f,	
				40f,0,0,
				0,0,0,
				
				40f,0f,0f,	
				5f,7f,17f,
				0,10f,10f,
				
		};
		
		float[] textureCoords = {
				
				0,0,
				0,1,
				1,1,
				
				0,0,
				0,1,
				1,1,
				
				0,0,
				0,1,
				1,1,

				
		};
		
		int[] indices = {
				0,1,2,
				3,4,5,
				6,7,8

		};*/
		
		RawModel model = loader.loadToVAO(vertices,textureCoords,indices);
		
		TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("resources/planeimg.jpg")));
		
		Entity entity = new Entity(staticModel, new Vector3f(0,0,-5),0,0,0,0.1f);
		
		Camera camera = new Camera();
		
		while(!Display.isCloseRequested()){
			entity.increaseRotation(0.5f, 0.5f, 0.5f);
			camera.move();
			renderer.prepare();
			shader.start();
			shader.loadViewMatrix(camera);
			renderer.render(entity,shader);
			shader.stop();
			DisplayManager.updateDisplay();
		}

		shader.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
