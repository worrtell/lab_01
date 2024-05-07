import java.util.ArrayList;

import static java.util.Arrays.asList;

public class Main {

    static ArrayList<Thread> threads;
    static ArrayList<Work> works;
    static ArrayList<Worker> workers;

    public static void main(String[] args) throws InterruptedException {
        works = createWorks(new ArrayList<>());
        threads = createThreads(new ArrayList<>());
        threads.get(threads.size()-1).join();
        workers = createWorkers(new ArrayList<>());
        workers.forEach(Worker::timePlaying);
    }
    static class Work implements Runnable {
        private int name;
        private ArrayList<Work> dependencies;
        private int duration;
        long startTime;
        long endTime;

        public Work(int name, ArrayList<Work> dependencies, int duration) {
            this.name = name;
            this.dependencies = dependencies;
            this.duration = duration;
        }
        public int getName() {
            return name;
        }
        @Override
        public void run() {
            dependencies.forEach(work -> {
                try {
                    threads.get(work.getName()).join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            System.out.println("Start " + name);
            startTime = System.nanoTime();
            try {
                Thread.sleep(1000L*duration);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            endTime = System.nanoTime();
            System.out.println("End " + name);
        }
    }
    public static ArrayList<Thread> createThreads(ArrayList<Thread> threads) throws InterruptedException {
        for (int i = 0; i <= 16; i++) {
            threads.add(new Thread(works.get(i)));
            threads.get(i).start();
        }
        return threads;
    }
    public static ArrayList<Work> createWorks(ArrayList<Work> works) {
        works.add(new Work(0, new ArrayList<>(),0));
        for (int i = 1; i <= 16; i++) {
            int d = 0;
            ArrayList<Work> temp = new ArrayList<>();
            switch (i) {
                case 1:
                    d = 7;
                    temp.add(works.get(0));
                    break;
                case 2, 3:
                    d = 7;
                    temp.add(works.get(1));
                    break;
                case 4:
                    d = 14;
                    temp.add(works.get(1));
                    break;
                case 5:
                    d = 4;
                    temp.add(works.get(4));
                    break;
                case 6:
                    d = 14;
                    temp.add(works.get(4));
                    break;
                case 7:
                    d = 3;
                    temp.add(works.get(6));
                    break;
                case 8:
                    d = 1;
                    temp.add(works.get(2));
                    temp.add(works.get(7));
                    break;
                case 9:
                    d = 1;
                    temp.add(works.get(3));
                    temp.add(works.get(7));
                    break;
                case 10:
                    d = 3;
                    temp.add(works.get(5));
                    temp.add(works.get(8));
                    temp.add(works.get(9));
                    break;
                case 11:
                    d = 3;
                    temp.add(works.get(5));
                    temp.add(works.get(7));
                    break;
                case 12:
                    d = 7;
                    temp.add(works.get(8));
                    temp.add(works.get(9));
                    temp.add(works.get(11));
                    break;
                case 13:
                    d = 7;
                    temp.add(works.get(12));
                    break;
                case 14:
                    d = 1;
                    temp.add(works.get(12));
                    break;
                case 15:
                    d = 2;
                    temp.add(works.get(5));
                    temp.add(works.get(11));
                    temp.add(works.get(12));
                    break;
                case 16:
                    for (int j = 0; j <= 15; j++) {
                        temp.add(works.get(j));
                    }
                    break;
            }
            works.add(new Work(i, temp, d));
        }
        return works;
    }
    static ArrayList<Worker> createWorkers(ArrayList<Worker> workers) {
        workers.add(new Worker(1,"Каменщики", new ArrayList<>(asList(works.get(4),works.get(6)))));

        workers.add(new Worker(2,"Плотники", new ArrayList<>(asList(works.get(2),works.get(3),works.get(7),works.get(8),works.get(9)))));

        workers.add(new Worker(3,"Сатнехники", new ArrayList<>(asList(works.get(5),works.get(10),works.get(15)))));

        workers.add(new Worker(4,"Штукатуры", new ArrayList<>(asList(works.get(12),works.get(13)))));

        workers.add(new Worker(5,"Электрики", new ArrayList<>(asList(works.get(11),works.get(14)))));
        return workers;
    }
    static class Worker {
        int id;
        String name;
        ArrayList<Work> worksToDo;
        ArrayList<Long> startTime;
        ArrayList<Long> endTime;
        Worker(int id, String name, ArrayList<Work> worksToDo) {
            this.id = id;
            this.name = name;
            this.worksToDo = worksToDo;
            startTime = new ArrayList<>();
            endTime = new ArrayList<>();
            endTime.add(works.get(1).endTime);
        }

        long timePlaying() {
            long cnt = 0;
            long curEnd = works.get(1).endTime;
            long curStart;
            for (int i = 0; i < worksToDo.size(); i++) {
                curStart = worksToDo.get(i).startTime;
                cnt += curStart - curEnd;
                curEnd = worksToDo.get(i).endTime;
            }
            System.out.println(name + " - " + cnt);
            return cnt;
        }
    }
}


