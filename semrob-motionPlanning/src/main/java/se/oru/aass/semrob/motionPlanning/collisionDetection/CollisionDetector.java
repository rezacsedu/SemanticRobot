package se.oru.aass.semrob.motionPlanning.collisionDetection;

import com.vividsolutions.jts.geom.Coordinate;

public class CollisionDetector {

	private Coordinate collisionPoint;
	public CollisionDetector() {
		collisionPoint = null;
	}
	
	public CollisionInfo hasCollision(Line line, Box box) {

		Coordinate L1 = line.getFirstCoordinate();
		Coordinate L2 = line.getSecondCoordinate();
		Coordinate B1 = box.getSmallestCoordinate();
		Coordinate B2 = box.getLargestCoordinate();

		if (L2.x < B1.x && L1.x < B1.x)
			//return false;
			return new CollisionInfo(false, null);
		if (L2.x > B2.x && L1.x > B2.x)
			//return false;
			return new CollisionInfo(false, null);
		if (L2.y < B1.y && L1.y < B1.y)
			//return false;
			return new CollisionInfo(false, null);
		if (L2.y > B2.y && L1.y > B2.y)
			//return false;
			return new CollisionInfo(false, null);
		if (L2.z < B1.z && L1.z < B1.z)
			//return false;
			return new CollisionInfo(false, null);
		if (L2.z > B2.z && L1.z > B2.z)
			//return false;
			return new CollisionInfo(false, null);
		if (L1.x > B1.x && L1.x < B2.x && L1.y > B1.y && L1.y < B2.y && L1.z > B1.z && L1.z < B2.z) {
			collisionPoint = L1;
			//return true;
			return new CollisionInfo(true, L1);
		}

		if ((getIntersection(L1.x - B1.x, L2.x - B1.x, L1, L2) && InBox(B1, B2, 1))
				|| (getIntersection(L1.y - B1.y, L2.y - B1.y, L1, L2) && InBox(B1, B2, 2))
				|| (getIntersection(L1.z - B1.z, L2.z - B1.z, L1, L2) && InBox(B1, B2, 3))
				|| (getIntersection(L1.x - B2.x, L2.x - B2.x, L1, L2) && InBox(B1, B2, 1))
				|| (getIntersection(L1.y - B2.y, L2.y - B2.y, L1, L2) && InBox(B1, B2, 2))
				|| (getIntersection(L1.z - B2.z, L2.z - B2.z, L1, L2) && InBox(B1, B2, 3)))
			//return true;
			return new CollisionInfo(true, collisionPoint);

		//return false;
		return new CollisionInfo(false, null);
	}

	private boolean getIntersection(double fDst1, double fDst2, Coordinate p1, Coordinate p2) {
		collisionPoint = null;
		if ((fDst1 * fDst2) > 0.0f)
			return false;
		if (fDst1 == fDst2)
			return false;
		double x = p1.x + (p2.x - p1.x) * (-fDst1 / (fDst2 - fDst1));
		double y = p1.y + (p2.y - p1.y) * (-fDst1 / (fDst2 - fDst1));
		double z = p1.z + (p2.z - p1.z) * (-fDst1 / (fDst2 - fDst1));
		collisionPoint = new Coordinate(x, y, z);
		return true;
	}

	private boolean InBox(Coordinate B1, Coordinate B2, int Axis) {
		if (Axis == 1 && collisionPoint.z >= B1.z && collisionPoint.z <= B2.z && collisionPoint.y >= B1.y
				&& collisionPoint.y <= B2.y)
			return true;
		if (Axis == 2 && collisionPoint.z >= B1.z && collisionPoint.z <= B2.z && collisionPoint.x >= B1.x
				&& collisionPoint.x <= B2.x)
			return true;
		if (Axis == 3 && collisionPoint.x >= B1.x && collisionPoint.x <= B2.x && collisionPoint.y >= B1.y
				&& collisionPoint.y <= B2.y)
			return true;
		return false;
	}
	
	public static class CollisionInfo {
		private boolean hasCollision;
		private Coordinate collisionPoint;
		public CollisionInfo(boolean hasCollision, Coordinate collisionPoint) {
			this.hasCollision = hasCollision;
			this.collisionPoint = collisionPoint;
		}
		public Coordinate getCollisionPoint() {
			return collisionPoint;
		}
		public boolean getCollisionStatus() {
			return hasCollision;
		}
	}

}
