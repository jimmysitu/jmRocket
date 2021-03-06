import targets
import testlib

class examplefpgaHart(targets.Hart):
    # This isn't generically true, but it's true enough for the Default*Configs in this code for now.
    # to get these tests to pass.
    xlen = 64
    ram = 0x80000000
    ram_size = 0x4000
    instruction_hardware_breakpoint_count = 2
    misa = 0x800000000014112d
    pass

class examplefpga(targets.Target):
    harts = [examplefpgaHart()]
    timeout_sec = 6000
    server_timeout_sec = 60*60
    openocd_config_path = "examplefpga.cfg"

    # For OpenOCD
    openocd_config = "examplefpga.cfg"
    ram = 0x80000000
    ram_size = 0x4000

    def create(self):
        print("STARTING A SIMULATION")
        print(self.sim_cmd)
        return testlib.VcsSim(sim_cmd=self.sim_cmd, debug=False)
