<script lang="ts">
  import Trash from "$assets/icons/trash.svg?component";
  import Edit from "$assets/icons/settingVert.svg?component";
  import Modal from "$components/ui/Modal.svelte";

  import type { PageData } from "./$types";
  export let data: PageData;

  let editModal = false;
  let addUserModal = false;
  let selectedUser = null;

  let exampleData = [
    {
      username: "admin",
      firstName: "Admin",
      lastName: "Admin",
      email: "admin@admin.com",
    },
    {
      username: "admin",
      firstName: "Admin",
      lastName: "Admin",
      email: "admin@admin.com",
    },
    {
      username: "admin",
      firstName: "Admin",
      lastName: "Admin",
      email: "admin@admin.com",
    },
    {
      username: "admin",
      firstName: "Admin",
      lastName: "Admin",
      email: "admin@admin.com",
    },
  ];
</script>

<h1>User</h1>

{#if editModal}
  <Modal open={editModal} on:close={() => (editModal = false)}>
    <h1>Username</h1>
  </Modal>
{/if}

<btn class="btn btn-primary flex justify-center w-fit mx-auto m-4">Add User</btn
>

<div class="overflow-x-auto flex justify-center">
  <table class="table table-compact table-zebra  2-xl">
    <thead class="">
      <tr>
        <th>ID</th>
        <th>Username</th>
        <th>firstName</th>
        <th>lastName</th>
        <th>Email</th>
        <th>Delete</th>
        <th>Edit</th>
      </tr>
    </thead>
    <tbody>
      {#each exampleData as row, i}
        <tr>
          <td>
            <span>{i + 1}</span>
          </td>
          <td>
            {row.username}
          </td>
          <td>
            {row.firstName}
          </td>
          <td>
            {row.lastName}
          </td>
          <td>
            {row.email}
          </td>
          <td>
            <!-- // TODO: should be a form action -->
            <button class="" on:click={() => (editModal = true)}
              ><Trash class="hover:fill-primary dark:fill-white" /></button
            >
          </td>
          <td>
            <button class="" on:click={() => (editModal = true)}
              ><Edit
                class="hover:stroke-primary dark:stroke-white stroke-black stroke-4"
              /></button
            >
          </td>
        </tr>
      {/each}
    </tbody>
  </table>
</div>
