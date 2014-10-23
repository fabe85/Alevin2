      #
      # VNE problem
      #
      # This finds the optimal solution to the optimal LP relaxation of the VNE problem
      #

      /* sets */
      set SNnodes;
      set SNUnmappedNodes within SNnodes;
      set SNlinks within (SNnodes cross SNnodes);
      set SNUnmappedLinks within SNlinks;
      set SNtotal within (SNnodes cross SNnodes);
      set VNnodes;
      set VNlinks within (VNnodes cross VNnodes);
      set candidateNodes within (VNnodes cross SNnodes);

      /* parameters */
      param LinkCapacity {(i,j) in SNlinks};
      param SNodeCapacity {i in SNnodes};
      param VLinkDemand {(k,l) in VNlinks};
      param VNodeDemand {i in VNnodes};
      param HHFactor;
      param RoundingFactor;
      /* variables*/
      var x {VNnodes cross SNnodes}, binary;
      var z {SNtotal cross VNlinks}, binary;
      var flow {SNtotal cross SNlinks} >= 0;
      var SLinkDemand {SNtotal} >= 0;
      var rho {SNlinks}, binary;
      var alpha {SNUnmappedNodes}, binary;

      /* objective function */
      minimize min: sum{i in SNUnmappedNodes} alpha[i] + sum{(i,j) in SNUnmappedLinks} rho[i,j];


      /* Constraints */
      /************** New Z Demand Constraint ***********************************/
      s.t. SLinkDemandConst{(i,j) in SNtotal}: sum{(k,l) in VNlinks} if(i<>j) then (VLinkDemand[k,l]*z[i,j,k,l]) else 0 = SLinkDemand[i,j];
      s.t. zConstraintI{(k,l) in VNlinks, i in SNnodes}: sum{(i,j) in SNtotal}z[i,j,k,l] = x[k,i];
      s.t. zConstraintII{(k,l) in VNlinks, j in SNnodes}: sum{(i,j) in SNtotal}z[i,j,k,l] = x[l,j];
      /******************Flow Conservation Equations*****************************/
      s.t. SourceI{(i,j) in SNtotal}: sum{(i,l) in SNlinks} flow[i,j,i,l] = SLinkDemand[i,j];
      s.t. SourceII{(i,j) in SNtotal}: sum{(k,i) in SNlinks} if(k<>i || i<>j) then flow[i,j,k,i] = 0;
      s.t. DestI{(i,j) in SNtotal}: sum{(k,j) in SNlinks} flow[i,j,k,j] = SLinkDemand[i,j];
      s.t. DestII{(i,j) in SNtotal}: sum{(j,l) in SNlinks} if(j<>l || i<>j) then flow[i,j,j,l] = 0;
      s.t. Flowcon{h in SNnodes, (i,j) in SNtotal}: sum{(h,l) in SNlinks} (if (h<>i and h<>j) then flow[i,j,h,l] else 0) - sum{(k,h) in SNlinks} (if (h<>i and h<>j) then flow[i,j,k,h] else 0) = 0;
      /************** Capacity Constraints ***********************************/
      s.t. CapacityLinkDem{(i,j) in SNlinks}: sum{(k,l) in SNtotal} flow[k,l,i,j]  <= LinkCapacity[i,j]*rho[i,j];
      s.t. CapacityNodeDemI{i in SNnodes}: sum{k in VNnodes}(x[k,i]*VNodeDemand[k]) + sum{(k,l) in SNtotal} (sum{(i,j) in SNlinks}(if(k<>i) then flow[k,l,i,j]*HHFactor)) <= SNodeCapacity[i];
      /**************** Coherence Constraint *********************************/
      s.t. CoherenceConst{(i,j) in SNtotal, k in VNnodes, l in VNnodes}: if((k,l) in VNlinks) then x[k,i] + x[l,j] - z[i,j,k,l] else 0 <= 1;
      s.t. CandidatesBelonging{k in VNnodes}: sum{(k,i) in candidateNodes} x[k,i] = 1;
      /**************** Active Node Constraints ******************************/
      s.t. ActiveNodesI{i in SNUnmappedNodes}:  alpha[i] <= (sum{(i,j) in SNlinks} rho [i,j]) + (sum{(j,i) in SNlinks} rho [j,i]);
      s.t. ActiveNodesII{i in SNUnmappedNodes}:  alpha[i]*RoundingFactor >= (sum{(i,j) in SNlinks} rho [i,j]) + (sum{(j,i) in SNlinks} rho [j,i]);

      /** It is avoided, more than one virtual node is allowed to stay in a substrate node 
	  s.t. NodeOverload{i in SNnodes}: sum{k in VNnodes} x[k,i] <= 1; **/