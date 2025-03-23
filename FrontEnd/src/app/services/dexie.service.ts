import { Injectable } from "@angular/core";
import Dexie from 'dexie'
import { User } from "../model/workout.model";


Injectable()
export class DexieService extends Dexie{

    users:Dexie.Table<User,string>
    constructor() {
        super('WorkoutDB');
        this.version(1).stores({
          users: 'name',  
        });
        this.users = this.table('users');
      }

      addUser(user: User) {
        return this.users.add(user);
      }

      updateUser(user: User) {
        return this.users.put(user);
      }

      deleteUser(name: string) {
        return this.users.delete(name);
      }
      getUser(name: string) {
        return this.users.get(name);
      }

}