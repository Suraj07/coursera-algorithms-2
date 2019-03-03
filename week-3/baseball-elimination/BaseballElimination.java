import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {

	private Map<String, Integer> teamMap;
	private int[] wins;
	private int[] losses;
	private int[] remaining;
	private int[][] against;
	private boolean[] eliminated;
	private String[] teams;
	private List<List<String>> eliminationCertificate;

	// create a baseball division from given filename in format specified below
	public BaseballElimination(String filename) {
		parseInput(filename);
		for (int xTeam = 0; xTeam < numberOfTeams(); xTeam++) {
			if (isTrivial(xTeam)) {
				continue;
			}
			FlowNetwork flowNetwork = constructFlowNetwork(xTeam);
			FordFulkerson fordFulkerson = new FordFulkerson(flowNetwork, 0, getTotalVertexCountForFlowNetwork() - 1);
			// after FoldFolkerson, if a team vertex is on the s-side of the
			// mincut, then it is one of the eliminationCertificate.
			List<String> ec = new ArrayList<>();
			for (int i = 0; i < numberOfTeams(); i++) {
				if (i == xTeam) {
					continue;
				}
				int vertex = getTeamVertexInFlowNetwork(i, xTeam);
				if (fordFulkerson.inCut(vertex)) {
					ec.add(teams[i]);
				}
			}
			if (ec.isEmpty()) {
				eliminated[xTeam] = false;
				eliminationCertificate.set(xTeam, null);
			} else {
				eliminated[xTeam] = true;
				eliminationCertificate.set(xTeam, ec);
			}
		}
	}

	private boolean isTrivial(int xTeam) {
		for (int i = 0; i < numberOfTeams(); i++) {
			if (i == xTeam) {
				continue;
			}
			double capacity = wins[xTeam] + remaining[xTeam] - wins[i];
			if (capacity < 0) {
				eliminated[xTeam] = true;
				List<String> ec = new ArrayList<>();
				ec.add(teams[i]);
				eliminationCertificate.set(xTeam, ec);
				return true;
			}
		}
		return false;
	}

	private FlowNetwork constructFlowNetwork(int xTeam) {
		FlowNetwork flowNetwork = new FlowNetwork(getTotalVertexCountForFlowNetwork());
		int gamevertex = 1;
		for (int i = 0; i < numberOfTeams(); i++) {
			for (int j = i + 1; j < numberOfTeams(); j++) {
				if ((i == xTeam) || (j == xTeam)) {
					continue;
				}
				FlowEdge e = new FlowEdge(0, gamevertex, against[i][j]);
				flowNetwork.addEdge(e);
				flowNetwork.addEdge(
						new FlowEdge(gamevertex, getTeamVertexInFlowNetwork(i, xTeam), Double.POSITIVE_INFINITY));
				flowNetwork.addEdge(
						new FlowEdge(gamevertex, getTeamVertexInFlowNetwork(j, xTeam), Double.POSITIVE_INFINITY));
				gamevertex++;
			}
		}

		for (int i = 0; i < numberOfTeams(); i++) {
			if (i == xTeam) {
				continue;
			}
			double capacity = wins[xTeam] + remaining[xTeam] - wins[i];
			flowNetwork.addEdge(new FlowEdge(getTeamVertexInFlowNetwork(i, xTeam),
					getTotalVertexCountForFlowNetwork() - 1, capacity));
		}
		return flowNetwork;
	}

	private int getTeamVertexInFlowNetwork(int team, int xTeam) {
		int vertex;
		if (team > xTeam) {
			// team vertex = source + numOfGameVertices + (team-vertex +
			// 1) - 1(as team > xteam) - 1(as index starts from 0)
			vertex = (1 + getNumOfGameVertices() + (team + 1) - 1) - 1;
		} else {
			// team vertex = source + numOfGameVertices + (team-vertex +
			// 1) - 1(as index starts from 0)
			vertex = (1 + getNumOfGameVertices() + (team + 1)) - 1;
		}
		return vertex;
	}

	private int getTotalVertexCountForFlowNetwork() {
		return getNumOfGameVertices() + numberOfTeams() + 2 - 1;
	}

	private int getNumOfGameVertices() {
		int numOfTeams = numberOfTeams() - 1;
		int numOfGameVertices = (numOfTeams * (numOfTeams - 1)) / 2;
		return numOfGameVertices;
	}

	private void parseInput(String filename) {
		In in = new In(filename);
		int numberOfTeams = in.readInt();
		teamMap = new HashMap<>(numberOfTeams);
		wins = new int[numberOfTeams];
		losses = new int[numberOfTeams];
		remaining = new int[numberOfTeams];
		against = new int[numberOfTeams][];
		eliminated = new boolean[numberOfTeams];
		eliminationCertificate = new ArrayList<List<String>>();
		teams = new String[numberOfTeams];
		for (int index = 0; index < numberOfTeams; index++) {
			String team = in.readString();
			teamMap.put(team, index);
			wins[index] = in.readInt();
			losses[index] = in.readInt();
			remaining[index] = in.readInt();
			int[] againstGames = new int[numberOfTeams];
			for (int j = 0; j < numberOfTeams; j++) {
				againstGames[j] = in.readInt();
			}
			this.against[index] = againstGames;
			this.eliminated[index] = false;
			this.eliminationCertificate.add(index, null);
			this.teams[index] = team;
		}
	}

	// number of teams
	public int numberOfTeams() {
		return teamMap.size();
	}

	// all teams
	public Iterable<String> teams() {
		return Arrays.asList(teams);
	}

	// number of wins for given team
	public int wins(String team) {
		checkValidTeam(team);
		return wins[teamMap.get(team)];
	}

	// number of losses for given team
	public int losses(String team) {
		checkValidTeam(team);
		return losses[teamMap.get(team)];
	}

	// number of remaining games for given team
	public int remaining(String team) {
		checkValidTeam(team);
		return remaining[teamMap.get(team)];
	}

	// number of remaining games between team1 and team2
	public int against(String team1, String team2) {
		checkValidTeam(team1);
		checkValidTeam(team2);
		return against[teamMap.get(team1)][teamMap.get(team2)];
	}

	// is given team eliminated?
	public boolean isEliminated(String team) {
		checkValidTeam(team);
		return eliminated[teamMap.get(team)];
	}

	// subset R of teams that eliminates given team; null if not eliminated
	public Iterable<String> certificateOfElimination(String team) {
		checkValidTeam(team);
		return eliminationCertificate.get(teamMap.get(team));
	}

	private void checkValidTeam(String team) {
		for (String t : teams()) {
			if (t.equals(team)) {
				return;
			}
		}
		throw new IllegalArgumentException("Not a valid team");
	}

	public static void main(String[] args) {
		BaseballElimination division = new BaseballElimination(args[0]);
		for (String team : division.teams()) {
			if (division.isEliminated(team)) {
				StdOut.print(team + " is eliminated by the subset R = { ");
				for (String t : division.certificateOfElimination(team)) {
					StdOut.print(t + " ");
				}
				StdOut.println("}");
			} else {
				StdOut.println(team + " is not eliminated");
			}
		}
	}

}
