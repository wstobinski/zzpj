export class User {
  firstName?: string;
  lastName?: string;
  email: string;
  role: "admin" | "captain" | "arbiter";
  uuid: number;
  modelId: number;

}
